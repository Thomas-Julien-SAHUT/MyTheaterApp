package com.example.mytheater.myApp.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.app.backup.BackupManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.mytheater.R
import com.nostra13.universalimageloader.core.ImageLoader
import com.example.mytheater.myApp.api.APIHelper
import com.example.mytheater.myApp.callbacks.TaskMoviesCallbacks
import com.example.mytheater.myApp.fragments.MoviesFragment.Callbacks
import com.example.mytheater.myApp.objects.Movie
import java.util.*

class DetailsFragment : Fragment(), TaskMoviesCallbacks {
    protected var theater = ""
    private var mCallbacks = sDummyCallbacks
    private var title: TextView? = null
    private var extra: TextView? = null
    private var display: TextView? = null
    private var poster: ImageView? = null
    private var certificate: TextView? = null
    private var synopsis: TextView? = null
    private var pressRating: RatingBar? = null
    private var pressRatingText: TextView? = null
    private var userRating: RatingBar? = null
    private var userRatingText: TextView? = null
    private var mTask: LoadMovieTask? = null
    private var titleToSet = false
    private var dialog: ProgressDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.retainInstance = true
    }

    override fun onResume() {
        super.onResume()
        if (displayedMovie != null && displayedMovie!!.synopsis.equals("", ignoreCase = true) && mTask == null) {
            mTask = LoadMovieTask(this)
            mTask!!.execute(displayedMovie!!.code)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_details, container, false)
        title = view.findViewById(R.id.details_title)
        extra = view.findViewById(R.id.details_extra)
        display = view.findViewById(R.id.details_display)
        poster = view.findViewById(R.id.details_poster)
        pressRating = view.findViewById(R.id.details_pressrating)
        pressRatingText = view.findViewById(R.id.details_pressrating_text)
        userRating = view.findViewById(R.id.details_userrating)
        userRatingText = view.findViewById(R.id.details_userrating_text)
        synopsis = view.findViewById(R.id.details_synopsis)
        certificate = view.findViewById(R.id.details_certificate)
        if (displayedMovie != null) {
            updateUI()
        }
        return view
    }

    override fun onDetach() {
        super.onDetach()
        mCallbacks = sDummyCallbacks
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        check(activity is Callbacks) { "Activity must implement fragment's callbacks." }
        mCallbacks = activity
        mCallbacks.setFragment(this)
        if (titleToSet) {
            getActivity()!!.title = displayedMovie!!.title
            titleToSet = false
        }
        if (toFinish) {
            mCallbacks.finishNoNetwork()
            toFinish = false
        }
    }

    fun shareMovie() {
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"
        sharingIntent.putExtra(Intent.EXTRA_TEXT, displayedMovie!!.getSharingText(theater))
        startActivity(Intent.createChooser(sharingIntent, "Partager le film..."))
    }

    fun displayTrailer() {
        class RetrieveTrailerTask : AsyncTask<Movie?, Void?, String?>() {

            override fun onPostExecute(trailerUrl: String?) {

                // Dismiss dialog
                if (dialog != null) {
                    try {
                        dialog!!.dismiss()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                if (trailerUrl == null && activity != null) {
                    Toast.makeText(activity, "Woops ! La bande annonce ne semble pas disponible...", Toast.LENGTH_SHORT).show()
                } else {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.setDataAndType(Uri.parse(trailerUrl), "video/mp4")
                        startActivity(intent)
                    } catch (e: ActivityNotFoundException) {
                        Toast.makeText(activity, "Vous devez avoir un lecteur vidéo pour afficher la bande-annonce de ce film.", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun doInBackground(vararg params: Movie?): String? {
                return params[0]?.let { APIHelper().downloadTrailerUrl(it) }
            }
        }
        RetrieveTrailerTask().execute(displayedMovie)
        dialog = ProgressDialog(activity)
        dialog!!.setMessage("Chargement de la bande annonce...")
        dialog!!.show()
    }

    fun updateUI() {
        title!!.text = displayedMovie!!.title
        var extraString = ""
        extraString += "<strong>Durée</strong> : " + displayedMovie!!.getDuration() + "<br />"
        if (displayedMovie!!.directors != "") extraString += "<strong>Directeur</strong> : " + displayedMovie!!.directors + "<br />"
        if (displayedMovie!!.actors != "") extraString += "<strong>Acteurs</strong> : " + displayedMovie!!.actors + "<br />"
        extraString += "<strong>Genre</strong> : " + displayedMovie!!.genres
        extra!!.text = Html.fromHtml(extraString)
        display!!.text = Html.fromHtml("<strong>" + theater + "</strong><br>" + displayedMovie!!.getDisplays())
        if (displayedMovie!!.certificateString == "") certificate!!.visibility = View.GONE else certificate!!.text = displayedMovie!!.certificateString
        ImageLoader.getInstance().displayImage(displayedMovie!!.getPosterUrl(2), poster)
        poster!!.setOnClickListener { v -> mCallbacks.onItemSelected(-1, this@DetailsFragment, v) }
        pressRating!!.progress = displayedMovie!!.getPressRating()
        if (displayedMovie!!.pressRating == "0") pressRatingText!!.text = "" else if (displayedMovie!!.pressRating.length > 3) pressRatingText!!.text = displayedMovie!!.pressRating.substring(0, 3) else pressRatingText!!.text = displayedMovie!!.pressRating
        userRating!!.progress = displayedMovie!!.getUserRating()
        if (displayedMovie!!.userRating == "0") userRatingText!!.text = "" else if (displayedMovie!!.userRating.length > 3) userRatingText!!.text = displayedMovie!!.userRating.substring(0, 3) else userRatingText!!.text = displayedMovie!!.userRating
        synopsis!!.text = if (displayedMovie!!.synopsis == "") "Chargement du synopsis..." else Html.fromHtml(
            displayedMovie!!.synopsis)
        if (activity != null) {
            activity!!.title = displayedMovie!!.title
        } else {
            titleToSet = true
        }
    }

    override fun updateListView(movies: ArrayList<Movie?>?) {
        // TODO Auto-generated method stub
    }

    override fun finishNoNetwork() {
        mCallbacks.finishNoNetwork()
    }

    override fun setArguments(args: Bundle?) {
        super.setArguments(args)
        if (arguments!!.containsKey(ARG_ITEM_ID)) {
            val idItem = arguments!!.getInt(ARG_ITEM_ID)
            displayedMovie = MoviesFragment.getMovies()!!.get(idItem)
        }
        if (arguments!!.containsKey(ARG_THEATER_NAME)) {
            theater = arguments!!.getString(ARG_THEATER_NAME).toString()
        }
    }

    private inner class LoadMovieTask(fragment: DetailsFragment) : AsyncTask<String?, Void?, Movie?>() {
        private val preferences: SharedPreferences
        private val ctx: Context?

        override fun onPostExecute(resultsList: Movie?) {
            mCallbacks.setIsLoading(false)
            displayedMovie = resultsList
            updateUI()
        }

        init {
            ctx = fragment.activity
            preferences = ctx!!.getSharedPreferences("synopsis", Context.MODE_PRIVATE)
            mCallbacks.setIsLoading(true)
        }

        @SuppressLint("NewApi")
        override fun doInBackground(vararg params: String?): Movie? {
            // Try to read synopsis from cache
            val movieCode = displayedMovie!!.code
            val cache = preferences.getString(movieCode, "")
            if (cache != "") {
                Log.i("cache-hit", "Getting synopsis from cache for $movieCode")
                if (cache != null) {
                    displayedMovie!!.synopsis = cache
                }
            } else {
                Log.i("cache-miss", "Remote loading synopsis for $movieCode")
                displayedMovie = APIHelper().findMovie(displayedMovie!!)
                val synopsis = displayedMovie!!.synopsis
                val ed = preferences.edit()
                ed.putString(movieCode, synopsis)
                ed.apply()
                BackupManager(ctx).dataChanged()
            }
            return displayedMovie
        }
    }

    companion object {
        const val ARG_ITEM_ID = "item_id"
        const val ARG_THEATER_NAME = "theater_name"
        @JvmField
        var displayedMovie: Movie? = null
        private var toFinish = false
        private val sDummyCallbacks: Callbacks = object : Callbacks {
            override fun onItemSelected(position: Int, source: Fragment?, currentView: View?) {}
            override fun setFragment(fragment: Fragment?) {}
            override fun setIsLoading(isLoading: Boolean?) {}
            override fun finishNoNetwork() {
                toFinish = true
            }
        }
    }
}