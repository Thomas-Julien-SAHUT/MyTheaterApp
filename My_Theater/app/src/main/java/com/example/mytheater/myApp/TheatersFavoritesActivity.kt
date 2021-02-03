package com.example.mytheater.myApp

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import com.example.mytheater.R
import com.example.mytheater.myApp.db.DBHelper
import com.example.mytheater.myApp.db.DBHelper.getFavorites
import com.example.mytheater.myApp.objects.Theater
import java.util.*

class TheatersFavoritesActivity : TheatersActivity() {
    var favorites: ArrayList<Theater>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTitle(R.string.app_name)

        // Display warning when using alpha version
        try {
            val pInfo = packageManager.getPackageInfo(packageName, 0)
            val version = pInfo.versionName
            if (version[version.length - 1] == 'a') {
                Toast.makeText(
                    this,
                    "Vous testez actuellement la version α de MyTheater.\nBons films !",
                    Toast.LENGTH_LONG
                ).show()
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        favorites = getFavorites(this)
        if (favorites!!.size > 0) {
            theaters = favorites as ArrayList<Theater>
        } else {
            // No favorites yet. Display theaters around
            val intent = Intent(this, TheatersSearchGeoActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    public override fun onResume() {
        favorites = getFavorites(this)
        theaters = favorites as ArrayList<Theater>
        super.onResume()
    }

    override fun retrieveResults(vararg queries: String?): ArrayList<Theater> {
        return ArrayList<Theater>()
    }
}