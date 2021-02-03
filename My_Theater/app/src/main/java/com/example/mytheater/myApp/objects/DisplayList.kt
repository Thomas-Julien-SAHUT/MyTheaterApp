package com.example.mytheater.myApp.objects

import org.json.JSONArray

/**
 * Holds all movies in a theater.
 *
 * @author neamar
 */
class DisplayList {
    /**
     * Flag set to true when no internet is available on the device.
     */
    var noDataConnection = false
    var theater = Theater()
    var jsonArray = JSONArray()
}