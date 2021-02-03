package com.example.mytheater.myApp.objects

import java.util.*

class Display : Comparable<Display> {
    var display: String? = null
    var screen = ""
    var theater: String? = null
    var isOriginalLanguage: Boolean? = null
    var is3D = false
    var isIMAX = false
    private var cachedDisplay: String? = null
    fun getDisplayDetails(shortDisplay: Boolean): String {
        var details = (if (theater != null) "<small>$theater</small>" else "") + (if (isOriginalLanguage!!) " <i>VO</i>" else "") + (if (is3D) " <strong>3D</strong>" else "") + if (isIMAX) " <strong>IMAX</strong>" else ""
        if (details == "") {
            details = "VF"
        }
        if (!shortDisplay) {
            details = "<u>$details</u>"
        }
        if (screen != "") {
            details += " <small><font color=\"silver\">(salle $screen)</font></small>"
        }
        if (!shortDisplay) {
            details += " :<br>"
        }
        return details
    }

    @JvmName("getDisplay1")
    fun getDisplay(): String {
        if (cachedDisplay != null) {
            return cachedDisplay as String
        }
        var optimisedDisplay = display
        // "Séances du"
        optimisedDisplay = optimisedDisplay!!.replace("Séances du ([a-z]{2})[a-z]+ ([0-9]+) [a-zéû]+ 20[0-9]{2} :".toRegex(), "$1 $2 :")

        // "(film à ..)"
        optimisedDisplay = optimisedDisplay.replace(" \\([^\\)]+\\)".toRegex(), "")

        // "15:30, "
        optimisedDisplay = optimisedDisplay.replace(",".toRegex(), "")

        // Same display each day ?
        var days = optimisedDisplay.replace(".+ : ".toRegex(), "").split("\r\n").toTypedArray()
        var isSimilar = true
        val firstOne = days[0]
        for (i in 1 until days.size) {
            if (firstOne != days[i]) {
                isSimilar = false
                break
            }
        }
        val today = Integer.toString(Calendar.getInstance()[Calendar.DAY_OF_MONTH])
        if (isSimilar && days.size == 7) {
            optimisedDisplay = if (!optimisedDisplay.contains(" $today :")) {
                lowlightHour("Semaine prochaine<br>TLJ : " + days[0]) + ""
            } else {
                lowlightHour("TLJ : " + days[0]) + ""
            }
        } else {
            // Lowlight every days but today.
            days = optimisedDisplay.split("\r\n").toTypedArray()
            optimisedDisplay = ""
            for (day in days) {
                if (!day.contains(" $today :")) {
                    optimisedDisplay += lowlightDay(day) + "<br>"
                } else {
                    // Note : it isn't "+=", but "=" : we remove past entries.
                    // Space required to fixing trimming bug.
                    optimisedDisplay = lowlightHour(day) + " <br>"
                }
            }

            // Remove final <br>
            optimisedDisplay = optimisedDisplay!!.substring(0, optimisedDisplay.length - 4)
        }
        cachedDisplay = optimisedDisplay
        return optimisedDisplay
    }

    private fun lowlightDay(day: String): String {
        return "<font color=\"silver\">$day</font>"
    }

    private fun lowlightHour(day: String): String {
        // Today : lowlight display in the past hours
        val now = Calendar.getInstance()
        val current_hour = now[Calendar.HOUR_OF_DAY]
        val current_minute = now[Calendar.MINUTE]
        val hours = day.replace(".+ : ".toRegex(), "").split(" ").toTypedArray()
        var nextVisibleDisplay = "$" // By default, last one.
        for (hour1 in hours) {
            val parts = hour1.split(":").toTypedArray()
            val hour = parts[0].toInt()
            val minute = parts[1].toInt()
            if (hour > current_hour || hour == current_hour && minute > current_minute) {
                nextVisibleDisplay = hour1
                break
            }
        }
        return day.replace("(.+ :)(.+) ("+(nextVisibleDisplay).toRegex()+")", "<strong>$1</strong><font color=\"#9A9A9A\">$2</font>$3")
    }

    /**
     * Returns an absolute value to sort this display. First display Imax
     * movies, then 3D, then VO, then VF.
     *
     * @return abstract value for comparison
     */
    private fun toInteger(): Int {
        var v = 0
        v += if (isIMAX) 10 else 0
        v += if (is3D) 100 else 0
        v += if (isOriginalLanguage!!) 1000 else 0
        return v
    }

    override fun compareTo(d: Display): Int {
        return toInteger() - d.toInteger()
    }
}