package com.example.mytheater.myApp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.example.mytheater.R
import com.example.mytheater.myApp.fragments.DetailsFragment
import com.example.mytheater.myApp.fragments.MoviesFragment

class DetailsActivity : FragmentActivity(), MoviesFragment.Callbacks {
    var detailsFragment: DetailsFragment? = null
    @SuppressLint("NewApi")
    public override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        // Title in action bar brings back one level
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            actionBar!!.setHomeButtonEnabled(true)
            actionBar!!.setDisplayHomeAsUpEnabled(true)
        }
        if (savedInstanceState == null) {
            val arguments = Bundle()
            // FileCodeMirrorFragment fragment = new FileCodeMirrorFragment();
            val fragment = DetailsFragment()
            arguments.putInt(
                DetailsFragment.ARG_ITEM_ID,
                intent.getIntExtra(DetailsFragment.ARG_ITEM_ID, -1)
            )
            arguments.putString(
                DetailsFragment.ARG_THEATER_NAME,
                intent.getStringExtra(DetailsFragment.ARG_THEATER_NAME)
            )
            fragment.arguments = arguments
            supportFragmentManager.beginTransaction().add(R.id.file_detail_container, fragment)
                .commit()
        }

        // Add a subtitle to display the current theater for this movie.
        if (intent.hasExtra(DetailsFragment.ARG_THEATER_NAME)) {
            actionBar!!.subtitle = intent.getStringExtra(DetailsFragment.ARG_THEATER_NAME)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.activity_details, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.menu_share -> {
                detailsFragment!!.shareMovie()
                true
            }
            R.id.menu_play -> {
                detailsFragment!!.displayTrailer()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onItemSelected(position: Int, source: Fragment?, currentView: View?) {
        if (source is DetailsFragment) {
            val intent = Intent(this, PosterViewerActivity::class.java)
            startActivity(intent)
        }
    }

    override fun setFragment(fragment: Fragment?) {
        detailsFragment = fragment as DetailsFragment?
    }

    override fun setIsLoading(isLoading: Boolean?) {
        setProgressBarIndeterminateVisibility(isLoading!!)
    }

    override fun finishNoNetwork() {
        Toast.makeText(
            this,
            "Impossible de télécharger les données. Merci de vérifier votre connexion ou de réessayer dans quelques minutes.",
            Toast.LENGTH_SHORT
        ).show()
        finish()
    }
}