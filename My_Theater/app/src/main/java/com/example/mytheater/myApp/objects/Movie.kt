package com.example.mytheater.myApp.objects

import android.text.Html
import java.util.*

/**
 * Holds datas for one movie.
 *
 * @author neamar
 */
class Movie : Comparable<Movie> {
    var code: String? = null
    @JvmField
    var trailerCode = ""
    var title: String? = null
    var poster: String? = null
    var directors = ""
    var actors = ""
    var genres = ""
    var synopsis = ""
    var duration = 0
    var certificate = 0
    var certificateString = ""
    var pressRating = "0"
    var userRating = "0"
    var displays = ArrayList<Display>()
    fun getDuration(): String {
        return if (duration > 0) (duration / 3600).toString() + "h" + String.format("%02d", duration / 60 % 60) else "NC"
    }

    private val shortCertificate: String
        private get() = when (certificate) {
            14004 -> "-18"
            14002 -> "-16"
            14044 -> "-12!"
            14001 -> "-12"
            14031 -> "-10"
            14035 -> "!"
            else -> ""
        }

    fun getPressRating(): Int {
        return (pressRating.toFloat() * 10).toInt()
    }

    fun getUserRating(): Int {
        return (userRating.toFloat() * 10).toInt()
    }

    val rating: Int
        get() {
            if (pressRating != "0" && userRating != "0") return (pressRating.toFloat() * 10 + userRating.toFloat() * 10).toInt() / 2 else if (pressRating == "0" && userRating != "0") return getUserRating() else if (pressRating != "0" && userRating == "0") return getPressRating()
            return 0
        }

    fun getDisplays(): String? {
        var ret: String? = ""
        for (i in displays.indices) {
            val displayDetails = displays[i].getDisplayDetails(false)
            ret += displayDetails
            ret += displays[i].getDisplay()
            if (i < displays.size - 1) {
                ret += "<br><br>"
            }
        }
        return ret
    }

    val displayDetails: String
        get() = if (certificate != 0) " <font color=\"#8B0000\">$shortCertificate</font>" else ""

    fun getPosterUrl(level: Int): String {
        var url: String? = null
        url = when (level) {
            1 -> "https://images.allocine.fr/r_215_290$poster"
            2 -> "https://images.allocine.fr/r_300_999$poster"
            3 -> "https://images.allocine.fr/r_1280_2000$poster"
            else -> ""
        }
        return url
    }

    /**
     * Generate a short text to be shared. Needs the theater this instance
     * refers to.
     */
    fun getSharingText(theater: String): String {
        var sharingText = ""
        sharingText += """${title} (${getDuration()})
"""
        val htmlDisplayDetails = "<strong>" + theater + "</strong>" + displayDetails + " :<br>" + getDisplays()
        sharingText += Html.fromHtml(htmlDisplayDetails).toString()
        return sharingText
    }

    override fun compareTo(movie: Movie): Int {
        return rating - movie.rating
    }
}