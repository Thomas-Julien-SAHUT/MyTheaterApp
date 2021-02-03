package com.example.mytheater.myApp.callbacks

import com.example.mytheater.myApp.objects.Theater
import java.util.*

interface TaskTheaterCallbacks {
    fun finishNoNetwork()
    fun onLoadOver(theaters: ArrayList<Theater?>?, isFavorite: Boolean, isGeoSearch: Boolean)
}