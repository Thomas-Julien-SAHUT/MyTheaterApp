package com.example.mytheater.myApp

import android.annotation.TargetApi
import android.app.SearchManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.core.app.NavUtils
import com.example.mytheater.myApp.api.APIHelper
import com.example.mytheater.myApp.objects.Theater
import java.io.IOException
import java.util.*

class TheatersSearchActivity : TheatersActivity() {
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (findViewById<View>(android.R.id.empty) as TextView).text =
            "Aucun résultat pour cette recherche."
        if (hasRestoredFromNonConfigurationInstance) {
            return
        }
        handleIntent(intent)

        // Title in action bar brings back one level
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            actionBar!!.setHomeButtonEnabled(true)
            actionBar!!.setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onNewIntent(intent: Intent) {
        setIntent(intent)
        handleIntent(intent)
    }

    fun handleIntent(intent: Intent) {
        val query = intent.getStringExtra(SearchManager.QUERY)
        title = "Recherche : $query"
        LoadTheatersTask().execute(query)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun retrieveResults(vararg queries: String?): ArrayList<Theater> {
        try {
            return queries[0]?.let { APIHelper().findTheaters(it) }!!
        } catch (e: IOException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }
        return ArrayList<Theater>()
    }
}