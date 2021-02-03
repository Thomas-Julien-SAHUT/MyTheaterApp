package com.example.mytheater.myApp

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.text.format.Time
import android.view.Menu
import android.view.View
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.mytheater.R
import com.example.mytheater.myApp.api.APIHelper
import com.example.mytheater.myApp.objects.Theater
import java.io.IOException
import java.util.*

class TheatersSearchGeoActivity : TheatersActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (findViewById<View>(android.R.id.empty) as TextView).text =
            "Aucun cinéma à proximité, utilisez la recherche."
        title = "Cinémas à proximité"
        if (hasRestoredFromNonConfigurationInstance) {
            return
        }
        retrieveLocation()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        if (requestCode == ON_LOCATION_PERMISSION_CHANGED && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            retrieveLocation()
        }
    }

    fun retrieveLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                ON_LOCATION_PERMISSION_CHANGED
            )
            return
        }
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        val locationEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        if (!hasLocationSupport()) {
            return
        }

        // Check location is enabled
        if (!locationEnabled) {
            val builder = AlertDialog.Builder(this)
            val res = resources
            builder.setMessage(res.getString(R.string.location_dialog_mess)).setCancelable(true)
                .setPositiveButton(res.getString(R.string.location_dialog_ok)) { dialog, id ->
                    dialog.cancel()
                    val settingsIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivityForResult(settingsIntent, WAITING_TO_ENABLE_LOCATION_PROVIDER)
                }
                .setNegativeButton(res.getString(R.string.location_dialog_cancel)) { dialog, id ->
                    onSearchRequested()
                    (findViewById<View>(android.R.id.empty) as TextView).setText(R.string.aucune_info_localisation)
                    dialog.cancel()
                }
            builder.create().show()
            return
        }
        val oldLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        val t = Time()
        t.setToNow()
        if (oldLocation != null && oldLocation.time - t.toMillis(true) < 3 * 60 * 60 * 1000) {
            LoadTheatersTask().execute(
                oldLocation.latitude.toString(),
                oldLocation.longitude.toString()
            )
        } else {
            (findViewById<View>(android.R.id.empty) as TextView).setText(R.string.recuperation_position)
            val listener: LocationListener = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    LoadTheatersTask().execute(
                        location.latitude.toString(),
                        location.longitude.toString()
                    )
                    locationManager.removeUpdates(this)
                }

                override fun onProviderDisabled(provider: String) {}
                override fun onProviderEnabled(provider: String) {}
                override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
            }
            locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                1000,
                10f,
                listener
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == WAITING_TO_ENABLE_LOCATION_PROVIDER) {
            retrieveLocation()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menu.findItem(R.id.menu_search_geo).isVisible = false
        return true
    }

    override fun retrieveResults(vararg queries: String?): ArrayList<Theater> {
        val lat = queries[0]
        val lon = queries[1]
        try {
            return APIHelper().findTheatersGeo(lat!!, lon!!)
        } catch (e: IOException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }
        return ArrayList<Theater>()
    }

    companion object {
        const val WAITING_TO_ENABLE_LOCATION_PROVIDER = 0
        const val ON_LOCATION_PERMISSION_CHANGED = 1
    }
}