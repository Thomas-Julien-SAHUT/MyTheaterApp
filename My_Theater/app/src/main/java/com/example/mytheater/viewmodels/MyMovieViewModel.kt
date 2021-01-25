package com.example.mytheater.viewmodels

import com.example.mytheater.models.MyMovie

data class MyMovieViewModel (val model: MyMovie) {
    val imageResource : Int = model.imageResource
    val name : String = model.name
    val date : String = model.date
}