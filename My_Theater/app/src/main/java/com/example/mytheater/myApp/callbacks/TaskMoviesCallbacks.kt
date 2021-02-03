package com.example.mytheater.myApp.callbacks

import com.example.mytheater.myApp.objects.Movie
import java.util.*

interface TaskMoviesCallbacks {
    fun updateListView(movies: ArrayList<Movie?>?)
    fun finishNoNetwork()
}