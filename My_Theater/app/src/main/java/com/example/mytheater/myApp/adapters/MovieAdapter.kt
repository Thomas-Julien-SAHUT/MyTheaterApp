package com.example.mytheater.myApp.adapters

import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import com.example.mytheater.R
import com.nostra13.universalimageloader.core.ImageLoader
import com.example.mytheater.myApp.objects.Movie
import java.util.*

class MovieAdapter(context: Context, textViewResourceId: Int, movies: ArrayList<Movie?>) : ArrayAdapter<Movie?>(context, textViewResourceId, movies) {
    var imageLoader: ImageLoader

    /**
     * Array list containing all the movies currently displayed
     */
    private var movies = ArrayList<Movie>()
    override fun getCount(): Int {
        return movies.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var v = convertView
        if (v == null) {
            val vi = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            v = vi.inflate(R.layout.item_movie, null)
        }
        val movie = movies[position]
        val movieTitle = v!!.findViewById<TextView>(R.id.listitem_movie_title)
        val movieExtra = v.findViewById<TextView>(R.id.listitem_movie_extra)
        val movieRating = v.findViewById<RatingBar>(R.id.listitem_movie_rating)
        val movieDisplay = v.findViewById<TextView>(R.id.listitem_movie_display)
        val moviePoster = v.findViewById<ImageView>(R.id.listitem_movie_poster)
        movieTitle.text = movie.title
        var description = movie.getDuration()
        description += movie.displayDetails
        val rating = movie.rating
        if (rating > 0) {
            movieRating.visibility = View.VISIBLE
            movieRating.max = 50
            movieRating.progress = movie.rating
        } else {
            movieRating.visibility = View.INVISIBLE
        }
        if (movie.displays.size == 1 && movie.displays[0].theater == null) {
            // Optimize layout when only one display available
            description += movie.displays[0].getDisplayDetails(true)
            movieDisplay.text = Html.fromHtml(movie.displays[0].getDisplay())
        } else {
            movieDisplay.text = Html.fromHtml(movie.getDisplays())
        }
        movieExtra.text = Html.fromHtml(description)
        moviePoster.setImageResource(R.drawable.stub)
        imageLoader.displayImage(movie.getPosterUrl(2), moviePoster)
        return v
    }

    override fun clear() {
        movies.clear()
        notifyDataSetChanged()
    }

    init {
        this.movies = movies.clone() as ArrayList<Movie>
        imageLoader = ImageLoader.getInstance()
    }
}