package com.example.mytheater.viewmodels

import com.example.mytheater.models.Movie

data class MovieViewModel(val model: Movie) {
    val imageResource : Int = model.imageResource
    val name : String = model.name
    val date : String = model.date
}