package com.example.mytheater.myApp

import android.annotation.TargetApi
import android.app.ActivityOptions
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Pair
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.core.app.NavUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.example.mytheater.R
import com.example.mytheater.myApp.fragments.DetailsEmptyFragment
import com.example.mytheater.myApp.fragments.DetailsFragment
import com.example.mytheater.myApp.fragments.MoviesFragment
import com.example.mytheater.myApp.objects.Theater

class MoviesActivity : FragmentActivity(), MoviesFragment.Callbacks {
    private var mTwoPane = false
    private var moviesFragment: MoviesFragment? = null
    private var detailsFragment: DetailsFragment? = null
    private var theater: String? = null
    private var theaterLocation = ""
    private var shareItem: MenuItem? = null
    private var trailerItem: MenuItem? = null
    public override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movies_list)
        mTwoPane = resources.getBoolean(R.bool.mTwoPane)
        theater = intent.getStringExtra("theater")
        title = intent.getStringExtra("theater")
        if (mTwoPane) {
            if (detailsFragment == null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.file_detail_container, DetailsEmptyFragment()).commit()
            } else {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.file_detail_container, detailsFragment!!).commit()
            }
        }
        // Title in action bar brings back one level
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            actionBar!!.setHomeButtonEnabled(true)
            actionBar!!.setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun setIsLoading(isLoading: Boolean?) {
        setProgressBarIndeterminateVisibility(isLoading!!)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        if (mTwoPane) {
            inflater.inflate(R.menu.activity_details, menu)
            shareItem = menu.findItem(R.id.menu_share)
            trailerItem = menu.findItem(R.id.menu_play)
            if (detailsFragment == null) {
                deactivateDetailsMenu(false)
            } else {
                activateDetailsMenu(false)
            }
        } else {
            inflater.inflate(R.menu.activity_movies, menu)
            if (theaterLocation != "") {
                menu.findItem(R.id.menu_map).isVisible = true
            }
            return true
        }
        return true
    }

    override fun onResume() {
        super.onResume()
        if (mTwoPane) {
            if (detailsFragment == null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.file_detail_container, DetailsEmptyFragment()).commit()
                deactivateDetailsMenu(true)
            } else {
                activateDetailsMenu(true)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                moviesFragment!!.clear()
                NavUtils.navigateUpFromSameTask(this)
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
            R.id.menu_map -> {
                val uri = "geo:0,0?q=$theaterLocation"
                try {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(uri)))
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(
                        this,
                        "Installez Google Maps pour afficher le plan !",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                super.onOptionsItemSelected(item)
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun setFragment(fragment: Fragment?) {
        if (fragment is MoviesFragment) {
            moviesFragment = fragment
        } else if (fragment is DetailsFragment) {
            detailsFragment = fragment
            activateDetailsMenu(true)
            if (DetailsFragment.displayedMovie == null || DetailsFragment.displayedMovie!!.trailerCode.isEmpty() && trailerItem != null) {
                trailerItem!!.isEnabled = false
                trailerItem!!.isVisible = false
                invalidateOptionsMenu()
            }
        }
    }

    fun setTheaterLocation(theater: Theater) {
        theaterLocation = theater.title + ", " + theater.location + " " + theater.zipCode
        if (!mTwoPane) {
            invalidateOptionsMenu()
        }
    }

    override fun onBackPressed() {
        if (mTwoPane && detailsFragment != null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.file_detail_container, DetailsEmptyFragment()).commit()
            detailsFragment = null
            deactivateDetailsMenu(true)
        } else {
            moviesFragment!!.clear()
            super.onBackPressed()
        }
    }

    @TargetApi(21)
    override fun onItemSelected(position: Int, source: Fragment?, currentView: View?) {
        if (source is MoviesFragment) {
            if (mTwoPane) {
                val arguments = Bundle()
                arguments.putInt(DetailsFragment.ARG_ITEM_ID, position)
                arguments.putString(DetailsFragment.ARG_THEATER_NAME, theater)
                detailsFragment = DetailsFragment()
                detailsFragment!!.arguments = arguments
                supportFragmentManager.beginTransaction()
                    .replace(R.id.file_detail_container, detailsFragment!!).commit()
            } else {
                val details = Intent(this, DetailsActivity::class.java)
                details.putExtra(DetailsFragment.ARG_ITEM_ID, position)
                details.putExtra(DetailsFragment.ARG_THEATER_NAME, theater)
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    startActivity(details)
                } else {
                    // Animation time!
                    val moviePoster = currentView!!.findViewById<View>(R.id.listitem_movie_poster)
                    val movieName = currentView.findViewById<View>(R.id.listitem_movie_title)
                    val options = ActivityOptions.makeSceneTransitionAnimation(
                        this,
                        Pair.create(moviePoster, "moviePoster"),
                        Pair.create(movieName, "movieName")
                    )
                    startActivity(details, options.toBundle())
                }
            }
        } else if (source is DetailsFragment) {
            startActivity(Intent(this, PosterViewerActivity::class.java))
        }
    }

    override fun finishNoNetwork() {
        Toast.makeText(
            this,
            "Impossible de télécharger les données. Merci de vérifier votre connexion ou de réessayer dans quelques minutes.",
            Toast.LENGTH_SHORT
        ).show()
        finish()
    }

    private fun activateDetailsMenu(rebuild: Boolean) {
        if (shareItem != null) {
            shareItem!!.isVisible = true
            shareItem!!.isEnabled = true
        }
        if (trailerItem != null) {
            trailerItem!!.isVisible = true
            trailerItem!!.isEnabled = true
        }
        if (rebuild) {
            invalidateOptionsMenu()
        }
    }

    private fun deactivateDetailsMenu(rebuild: Boolean) {
        if (shareItem != null) {
            shareItem!!.isEnabled = false
            shareItem!!.isVisible = false
        }
        if (trailerItem != null) {
            trailerItem!!.isEnabled = false
            trailerItem!!.isVisible = false
        }
        if (rebuild) {
            invalidateOptionsMenu()
        }
    }
}