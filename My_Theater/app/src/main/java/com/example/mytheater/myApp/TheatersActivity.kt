package com.example.mytheater.myApp

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.ListActivity
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.widget.ListView
import android.widget.TextView
import com.example.mytheater.R
import com.example.mytheater.myApp.adapters.TheaterAdapter
import com.example.mytheater.myApp.objects.Theater
import java.util.*
import kotlin.NullPointerException

@Suppress("DEPRECATION")
abstract class TheatersActivity : ListActivity() {
    protected var hasRestoredFromNonConfigurationInstance = false
    private var dialog: ProgressDialog? = null
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_theaters)
        setTitle(R.string.title_activity_theaters)
        val theaters = lastNonConfigurationInstance as? ArrayList<Theater>
        if (theaters != null) {
            listAdapter = TheaterAdapter(this, R.layout.item_theater, theaters)
            hasRestoredFromNonConfigurationInstance = true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.activity_theaters, menu)
        menu.findItem(R.id.menu_search_geo).isVisible = hasLocationSupport()
        menu.findItem(R.id.menu_unified).isEnabled = true
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_search -> {
                onSearchRequested()
                val searchIntent = Intent(this, TheatersSearchActivity::class.java)
                searchIntent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(searchIntent)
                true
            }
            R.id.menu_search_geo -> {
                val geoIntent = Intent(this, TheatersSearchGeoActivity::class.java)
                geoIntent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(geoIntent)
                true
            }
            R.id.menu_unified -> {
                    val theaters = theaters
                    val unified: MutableList<Theater> = theaters.subList(0, Math.min(7, theaters.size))
                    val codes = ArrayList<String>()
                    for (t in unified) {
                        t.code?.let { codes.add(it) }
                    }
                    val c = 0x2460 + unified.size - 1
                    val count = Character.toString(c.toChar()) + " "
                    val unifiedIntent = Intent(this@TheatersActivity, MoviesActivity::class.java)
                    unifiedIntent.putExtra("code", TextUtils.join(",", codes))
                    unifiedIntent.putExtra("theater", count + TextUtils.join(", ", unified))
                    startActivity(unifiedIntent)
                    super.onOptionsItemSelected(item)
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onListItemClick(l: ListView, v: View, position: Int, id: Long) {
        val theater = theaters[position]
        val intent = Intent(this, MoviesActivity::class.java)
        intent.putExtra("code", theater!!.code)
        intent.putExtra("theater", theater.title)
        startActivity(intent)
    }

    override fun onRetainNonConfigurationInstance(): Any {
        return (if (listAdapter != null) {
            null
        } else {
            (listAdapter as TheaterAdapter).theaters
        })!!
    }

    override fun onPause() {
        super.onPause()
        // Remove dialog when leaving activity
        dialog = null
    }

    protected var theaters: ArrayList<Theater> = ArrayList<Theater>()
        protected get() {
            try {
                val adapter = listAdapter as? TheaterAdapter
                if (adapter != null) {
                    return adapter.theaters
                }
                else{
                    return ArrayList<Theater>()
                }
            }catch (ex : NullPointerException){
                return ArrayList<Theater>()
            }
        }

    @JvmName("setTheaters1")
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    protected fun setTheaters(theaters: ArrayList<Theater>) {
        listAdapter = TheaterAdapter(this@TheatersActivity, R.layout.item_theater, theaters)
        invalidateOptionsMenu()
    }

    protected abstract fun retrieveResults(vararg queries: String?): ArrayList<Theater>
    @SuppressLint("InlinedApi")
    protected fun hasLocationSupport(): Boolean {
        val pm = packageManager
        return pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_NETWORK)
    }

    protected inner class LoadTheatersTask internal constructor() :
        AsyncTask<String?, Void?, ArrayList<Theater>>() {
        override fun onPreExecute() {
            if (dialog != null && dialog!!.isShowing) {
                dialog!!.dismiss()
            }
            dialog = ProgressDialog(this@TheatersActivity)
            dialog!!.setMessage("Recherche en cours...")
            dialog!!.show()
        }

        override fun doInBackground(vararg params: String?): ArrayList<Theater> {
            return retrieveResults(params.toString())
        }

        override fun onPostExecute(result: ArrayList<Theater>?) {
            if (dialog != null && dialog!!.isShowing) {
                dialog!!.dismiss()
            }
            if (theaters != null) {
                setTheaters(theaters)
            } else {
                (findViewById<View>(android.R.id.empty) as TextView).setText(R.string.aucune_connexion_internet)
            }
        }
    }
}