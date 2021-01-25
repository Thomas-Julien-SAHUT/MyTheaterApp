package com.example.mytheater.controllers.stub

import com.example.mytheater.R
import com.example.mytheater.models.Movie
import com.example.mytheater.models.MyMovie
import com.example.mytheater.viewmodels.MovieViewModel
import com.example.mytheater.viewmodels.MyMovieViewModel
import java.util.*
import kotlin.collections.ArrayList

object Stub {

    fun generateDummyList(size :Int): List<MovieViewModel>{
        val list = ArrayList<MovieViewModel>()
        for (i in 1 until size){
            val drawable = when (i % 3){
                0-> R.drawable.ic_baseline_local_movies_24
                1-> R.drawable.ic_baseline_movie_filter_24
                else-> R.drawable.ic_baseline_ondemand_video_24
            }

            val movie =  Movie(drawable,"Item $i", "Line ${Date()}")
            val item = MovieViewModel(movie)
            list+=item
        }
        return list
    }

    fun generateMyDummyList(size :Int): List<MyMovieViewModel>{
        val list = ArrayList<MyMovieViewModel>()
        for (i in 1 until size){
            val drawable = when (i % 3){
                0-> R.drawable.ic_baseline_local_movies_24
                1-> R.drawable.ic_baseline_movie_filter_24
                else-> R.drawable.ic_baseline_ondemand_video_24
            }

            val movie =  MyMovie(drawable,"Item $i", "Line ${Date()}")
            val item = MyMovieViewModel(movie)
            list+=item
        }
        return list
    }
}