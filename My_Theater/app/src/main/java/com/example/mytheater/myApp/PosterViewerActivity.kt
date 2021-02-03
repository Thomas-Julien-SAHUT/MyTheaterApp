package com.example.mytheater.myApp

import android.app.Activity
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import com.example.mytheater.R
import com.example.mytheater.myApp.fragments.DetailsFragment
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.assist.FailReason
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener

class PosterViewerActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.poster_viewer)
        val poster = findViewById<ImageView>(R.id.posterView)
        findViewById<View>(R.id.spinner).visibility = View.VISIBLE
        ImageLoader.getInstance().displayImage(
            DetailsFragment.displayedMovie!!.getPosterUrl(3),
            poster,
            object : ImageLoadingListener {
                override fun onLoadingStarted(imageUri: String, view: View) {}
                override fun onLoadingFailed(imageUri: String, view: View, failReason: FailReason) {
                    findViewById<View>(R.id.spinner).visibility = View.INVISIBLE
                }

                override fun onLoadingComplete(imageUri: String, view: View, loadedImage: Bitmap) {
                    findViewById<View>(R.id.spinner).visibility = View.INVISIBLE
                }

                override fun onLoadingCancelled(imageUri: String, view: View) {}
            })
    }
}