package com.example.mytheater.myApp.fragments

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.ListFragment
import com.example.mytheater.R
import com.example.mytheater.myApp.MoviesActivity
import com.example.mytheater.myApp.adapters.MovieAdapter
import com.example.mytheater.myApp.api.APIHelper
import com.example.mytheater.myApp.callbacks.TaskMoviesCallbacks
import com.example.mytheater.myApp.objects.DisplayList
import com.example.mytheater.myApp.objects.Movie
import com.example.mytheater.myApp.objects.Theater
import org.json.JSONArray
import org.json.JSONException
import java.util.*

class MoviesFragment : ListFragment(), TaskMoviesCallbacks {
    var movies: ArrayList<Movie?>? = null
    private var mCallbacks = sDummyCallbacks
    private var mActivatedPosition = ListView.INVALID_POSITION
    private var mTask: LoadMoviesTask? = null
    private var dialog: ProgressDialog? = null
    private var theater: Theater? = null
    override fun onResume() {
        super.onResume()
        if (movies == null && mTask == null) {
            val theaterCode = activity!!.intent.getStringExtra("code")
            mTask = theaterCode?.let { LoadMoviesTask(this, it) }
            mTask!!.execute(theaterCode)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION))
        }
        return inflater.inflate(R.layout.fragment_movies, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.retainInstance = true
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        // Add details footer to listView
        val text = TextView(activity)
        text.text = Html.fromHtml("<small><strong>TLJ</strong> : Tous Les Jours (jusqu'à mardi inclus)")
        text.gravity = Gravity.CENTER_HORIZONTAL
        val list = activity!!.findViewById<ListView>(android.R.id.list)
        list.addFooterView(text, null, false)
        if (movies == null && mTask == null) {
            val theaterCode = activity!!.intent.getStringExtra("code")
            mTask = theaterCode?.let { LoadMoviesTask(this, it) }
            mTask!!.execute(theaterCode)
        }
        super.onActivityCreated(savedInstanceState)
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        check(activity is Callbacks) { "Activity must implement fragment's callbacks." }
        mCallbacks = activity
        mCallbacks.setFragment(this)
        if (toFinish) {
            mCallbacks.finishNoNetwork()
            toFinish = false
        }
        if (dialogPending) {
            dialog = ProgressDialog(activity)
            dialog!!.setMessage("Chargement des séances en cours...")
            dialog!!.show()
        }
        if (toUpdate && movies != null) {
            updateListView(movies)
            toUpdate = false
        }
        if (theater != null) {
            (activity as MoviesActivity).setTheaterLocation(theater!!)
        }
    }

    override fun onDetach() {
        super.onDetach()
        if (dialog != null) {
            dialog!!.dismiss()
            dialog = null
        }
        mCallbacks = sDummyCallbacks
    }

    override fun onListItemClick(listView: ListView, view: View, position: Int, id: Long) {
        super.onListItemClick(listView, view, position, id)
        mCallbacks.onItemSelected(position, this, view)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (mActivatedPosition != AdapterView.INVALID_POSITION) {
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition)
        }
    }

    fun setActivateOnItemClick(activateOnItemClick: Boolean) {
        listView.choiceMode = if (activateOnItemClick) AbsListView.CHOICE_MODE_SINGLE else AbsListView.CHOICE_MODE_NONE
    }

    fun setActivatedPosition(position: Int) {
        if (position == AdapterView.INVALID_POSITION) {
            listView.setItemChecked(mActivatedPosition, false)
        } else {
            listView.setItemChecked(position, true)
        }
        mActivatedPosition = position
    }

    override fun updateListView(movies: ArrayList<Movie?>?) {
        currentMovies = movies
        this.movies = movies
        if (activity != null) {
            listAdapter = MovieAdapter(activity!!, R.layout.item_theater, movies!!)
        } else {
            toUpdate = true
        }
        mTask = null
    }

    override fun finishNoNetwork() {
        mCallbacks.finishNoNetwork()
    }

    fun clear() {
        if (currentMovies != null) {
            currentMovies!!.clear()
            currentMovies = null
            movies = null
            if (listAdapter != null) {
                (listAdapter as MovieAdapter).clear()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val pm = activity!!.packageManager
        if (!pm.hasSystemFeature(PackageManager.FEATURE_TOUCHSCREEN)) {
            listView.requestFocus()
        }
    }

    interface Callbacks {
        fun onItemSelected(position: Int, source: Fragment?, currentView: View?)
        fun setFragment(fragment: Fragment?)
        fun setIsLoading(isLoading: Boolean?)
        fun finishNoNetwork()
    }

    private inner class LoadMoviesTask internal constructor(private val fragment: MoviesFragment, theaterCode: String) : AsyncTask<String?, Void?, DisplayList>() {
        private val ctx: Context?
        private val theaterCode: String

        /**
         * Last values retrieved from previous run.
         */
        private var cache: String? = null

        /**
         * Timestamp for last update
         */
        private var lastCacheUpdate: Long? = null
        private var remoteDataHasChangedFromLocalCache = true
        override fun onPreExecute() {
            val sp = ctx!!.getSharedPreferences("theater-cache", Context.MODE_PRIVATE)
            lastCacheUpdate = sp.getLong("$theaterCode-date", 0)
            cache = sp.getString(theaterCode, "")
            if (cache != "") {
                // Display cached values
                try {
                    Log.i("cache-hit", "Getting display datas from cache for $theaterCode")
                    mCallbacks.setIsLoading(true)
                    val movies = APIHelper().formatMoviesList(JSONArray(cache), theaterCode)
                    fragment.updateListView(movies)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            } else {
                Log.i("cache-miss", "Remote loading first-time datas for $theaterCode")
                dialog = ProgressDialog(ctx)
                dialog!!.setMessage("Chargement des séances en cours...")
                dialog!!.show()
                dialogPending = true
            }
        }

        override fun onPostExecute(displayList: DisplayList) {
            mCallbacks.setIsLoading(false)
            if (dialog != null) {
                if (dialog!!.isShowing) dialog!!.dismiss()
            }
            dialogPending = false
            if (displayList.noDataConnection && activity != null) {
                // No data connection, so unable to update.
                // However our cache may be valid.
                val cacheDate = Date(lastCacheUpdate!!)
                val c = Calendar.getInstance()
                // If we're before wednesday and after start of next week, get back one week before setting day to Wednesay
                if (c[Calendar.DAY_OF_WEEK] < Calendar.WEDNESDAY) {
                    c.roll(Calendar.WEEK_OF_YEAR, -1)
                }
                c[Calendar.DAY_OF_WEEK] = Calendar.WEDNESDAY
                c[Calendar.HOUR_OF_DAY] = 0
                c[Calendar.MINUTE] = 0
                c[Calendar.SECOND] = 0
                val lastWednesday = c.time
                if (cacheDate.time > lastWednesday.time) {
                    Toast.makeText(ctx, "No connection, displaying datas from cache.", Toast.LENGTH_SHORT).show()
                    remoteDataHasChangedFromLocalCache = false
                } else {
                    val emptyText = activity!!.findViewById<View>(android.R.id.empty) as TextView
                    emptyText.setText(R.string.aucune_connexion_internet)
                }
            }

            // Update only if data changed
            if (remoteDataHasChangedFromLocalCache) {
                val movies = APIHelper().formatMoviesList(displayList.jsonArray, theaterCode)
                fragment.updateListView(movies)
            }
            theater = displayList.theater
            if (activity != null && theater!!.code != null) {
                (activity as MoviesActivity?)!!.setTheaterLocation(theater!!)
            }
        }

        init {
            ctx = fragment.activity
            this.theaterCode = theaterCode
        }

        override fun doInBackground(vararg params: String?): DisplayList {
            if (theaterCode != params[0]) {
                throw RuntimeException("Fragment misuse: theaterCode differs")
            }
            val displayList = APIHelper().downloadMoviesList(theaterCode)
            val jsonResults = displayList.jsonArray
            val newCache = jsonResults.toString()
            if (cache == newCache) {
                Log.i("cache-hit", "Remote datas equals local datas; skipping UI update.")
                remoteDataHasChangedFromLocalCache = false
            } else if (!displayList.noDataConnection) { // Do not overwrite cache with empty datas
                Log.i("cache-miss", "Remote data differs from local datas; updating UI")
                // Store in cache for future use
                // Also store the date of the day
                val ed = ctx!!.getSharedPreferences("theater-cache", Context.MODE_PRIVATE).edit()
                ed.putString(theaterCode, jsonResults.toString())
                ed.putLong("$theaterCode-date", Date().time)
                ed.apply()
                remoteDataHasChangedFromLocalCache = true
            }
            return displayList
        }
    }

    companion object {
        private const val STATE_ACTIVATED_POSITION = "activated_position"
        var currentMovies: ArrayList<Movie?>? = null
        private var toFinish = false
        private var dialogPending = false
        private var toUpdate = false
        private val sDummyCallbacks: Callbacks = object : Callbacks {
            override fun onItemSelected(position: Int, source: Fragment?, currentView: View?) {}
            override fun setFragment(fragment: Fragment?) {}
            override fun setIsLoading(isLoading: Boolean?) {}
            override fun finishNoNetwork() {
                toFinish = true
            }
        }

        fun getMovies(): ArrayList<Movie?>? {
            return currentMovies
        }
    }
}