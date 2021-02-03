package com.example.mytheater.myApp.api

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Base64
import android.util.Log
import com.example.mytheater.myApp.objects.Display
import com.example.mytheater.myApp.objects.DisplayList
import com.example.mytheater.myApp.objects.Movie
import com.example.mytheater.myApp.objects.Theater
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.UnsupportedEncodingException
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.SimpleDateFormat
import java.util.*

class APIHelper {
    /**
     * Download an url using GET.
     *
     */
    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    private fun downloadUrl(method: String, params: MutableMap<String, String>, mockUrl: String): String {
        Log.v(TAG, "Downloading $method")
        params["sed"] = SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().time)
        params["partner"] = PARTNER_KEY
        params["format"] = "json"
        val payload = StringBuilder()
        var firstLoop = true
        for ((key, value) in params) {
            if (firstLoop) {
                firstLoop = false
            } else {
                payload.append('&')
            }
            payload
                    .append(key)
                    .append("=")
                    .append(Uri.encode(value))
        }

        // base64_encode(sha1($method . http_build_query($params) . $this->_secret_key, true));
        val toSign = method + payload + SECRET_KEY
        Log.e("WTF", toSign)
        var messageDigest: MessageDigest? = null
        try {
            messageDigest = MessageDigest.getInstance("SHA-1")
            messageDigest.update(toSign.toByteArray(charset("UTF-8")))
            val bytes = messageDigest.digest()
            val encoded = Uri.encode(Base64.encodeToString(bytes, Base64.DEFAULT))
            val fixedEncoding = encoded.replace("%0A$".toRegex(), "")
            payload.append("&sig=").append(fixedEncoding)
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
            return ""
        }
        var url = "$BASE_URL$method?$payload"
        url = mockUrl

        Log.i(TAG, "URL: $url")
        // Setup the get request
        val httpGetRequest = URL(url)
        val urlConnection = httpGetRequest.openConnection() as HttpURLConnection
        val `is` = urlConnection.inputStream

        // Grab the response
        val reader: BufferedReader
        try {
            reader = BufferedReader(InputStreamReader(`is`, "UTF-8"))
            val builder = StringBuilder()
            var aux: String?
            while (reader.readLine().also { aux = it } != null) {
                builder.append(aux)
            }
            return builder.toString()
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
        return ""
    }

    @Throws(IOException::class)
    private fun downloadTheatersList(query: String): JSONArray {
        val params: MutableMap<String, String> = HashMap()
        params["filter"] = "theater"
        params["q"] = query
        params["count"] = "25"
        return try {
            val json = downloadUrl("search", params, "https://gist.githubusercontent.com/Neamar/9713818694c4c37f583c4d5cf4046611/raw/cinemas-search.json")

            // Instantiate a JSON object from the request response
            val jsonObject = JSONObject(json)
            val feed = jsonObject.getJSONObject("feed")
            if (feed.getInt("totalResults") > 0) feed.getJSONArray("theater") else JSONArray()
        } catch (e: JSONException) {
            // throw new RuntimeException("Unable to download theaters list.");
            JSONArray()
        }
    }

    @Throws(IOException::class)
    private fun downloadTheatersListGeo(lat: String, lon: String): JSONArray {
        val params: MutableMap<String, String> = HashMap()
        params["lat"] = lat
        params["long"] = lon
        params["radius"] = "50"
        params["count"] = "25"
        return try {
            val json = downloadUrl("theaterlist", params, "https://gist.githubusercontent.com/Neamar/9713818694c4c37f583c4d5cf4046611/raw/cinemas-gps.json")

            // Instantiate a JSON object from the request response
            val jsonObject = JSONObject(json)
            val feed = jsonObject.getJSONObject("feed")
            if (feed.getInt("totalResults") > 0) feed.getJSONArray("theater") else JSONArray()
        } catch (e: JSONException) {
            // throw new RuntimeException("Unable to download theaters list.");
            JSONArray()
        }
    }

    /**
     * Download all movies for the specified theater.
     *
     * @param theaterCode Code, or a comma separated list of code to load.
     */
    fun downloadMoviesList(theaterCode: String): DisplayList {
        val params: MutableMap<String, String> = HashMap()
        params["theaters"] = theaterCode
        val displayList = DisplayList()
        val json: String
        try {
            json = downloadUrl("showtimelist", params, "https://gist.githubusercontent.com/Neamar/9713818694c4c37f583c4d5cf4046611/raw/6f2ae30320e9e93807268f3a3772cdd8bba90987/cinema.json")
        } catch (e: Exception) {
            displayList.noDataConnection = true
            return displayList
        }
        try {
            // Instantiate a JSON object from the request response
            val jsonObject = JSONObject(json)
            val feed = jsonObject.getJSONObject("feed")
            val theaters = feed.getJSONArray("theaterShowtimes")
            // Iterate over each theaters
            for (i in 0 until theaters.length()) {
                val theater = theaters.getJSONObject(i)
                val theaterName = theater.getJSONObject("place").getJSONObject("theater").getString("name")
                if (theater.has("movieShowtimes")) {
                    val showtimes = theater.getJSONArray("movieShowtimes")
                    for (j in 0 until showtimes.length()) {
                        val showtime = showtimes.getJSONObject(j)
                        if (theaters.length() > 1) {
                            // Add theater name when multiple theaters returned
                            showtime.put("theater", theaterName)
                        }
                        displayList.jsonArray.put(showtime)
                    }
                }
            }

            // Only return theater when it is unique
            if (theaters.length() == 1) {
                val jsonTheater = theaters.getJSONObject(0).getJSONObject("place").getJSONObject("theater")
                displayList.theater.code = jsonTheater.getString("code")
                displayList.theater.title = jsonTheater.getString("name")
                displayList.theater.location = jsonTheater.getString("address")
                displayList.theater.zipCode = jsonTheater.getString("postalCode")
            }
        } catch (e: JSONException) {
            Log.e("JSON", "Error parsing JSON for $theaterCode")
            e.printStackTrace()
            // Keep our default empty array for displayList.jsonArray
        }
        return displayList
    }

    @Throws(IOException::class)
    fun findTheaters(query: String): ArrayList<Theater> {
        val resultsList = ArrayList<Theater>()
        val jsonResults = downloadTheatersList(query)
        for (i in 0 until jsonResults.length()) {
            var jsonTheater: JSONObject
            try {
                jsonTheater = jsonResults.getJSONObject(i)
                val theater = Theater()
                theater.code = jsonTheater.getString("code")
                theater.title = jsonTheater.getString("name")
                theater.location = jsonTheater.getString("address")
                theater.city = jsonTheater.getString("city")
                resultsList.add(theater)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        return resultsList
    }

    @Throws(IOException::class)
    fun findTheatersGeo(lat: String, lon: String): ArrayList<Theater> {
        val resultsList = ArrayList<Theater>()
        val jsonResults = downloadTheatersListGeo(lat, lon)
        for (i in 0 until jsonResults.length()) {
            var jsonTheater: JSONObject
            try {
                jsonTheater = jsonResults.getJSONObject(i)
                val theater = Theater()
                theater.code = jsonTheater.getString("code")
                theater.title = jsonTheater.getString("name")
                theater.location = jsonTheater.getString("address")
                theater.distance = jsonTheater.getDouble("distance")
                theater.city = jsonTheater.getString("city")
                resultsList.add(theater)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        return resultsList
    }

    private fun downloadMovie(movieCode: String): JSONObject {
        val params: MutableMap<String, String> = HashMap()
        params["code"] = movieCode
        params["profile"] = "small"
        return try {
            val json = downloadUrl("movie", params, "https://gist.githubusercontent.com/Neamar/9713818694c4c37f583c4d5cf4046611/raw/film.json")

            // Instantiate a JSON object from the request response
            val jsonObject = JSONObject(json)
            jsonObject.getJSONObject("movie")
        } catch (e: Exception) {
            // throw new RuntimeException("Unable to download movies list.");
            JSONObject()
        }
    }

    fun formatMoviesList(jsonResults: JSONArray, theaterCode: String): ArrayList<Movie?> {
        val moviesHash = HashMap<String, Movie?>()
        for (i in 0 until jsonResults.length()) {
            var jsonMovie: JSONObject
            var jsonShow: JSONObject
            try {
                jsonMovie = jsonResults.getJSONObject(i)
                jsonShow = jsonMovie.getJSONObject("onShow").getJSONObject("movie")
                val code = jsonShow.getString("code")
                var movie: Movie?
                movie = if (moviesHash.containsKey(code)) {
                    moviesHash[code]
                } else {
                    Movie()
                }
                movie!!.code = jsonShow.getString("code")
                movie.title = jsonShow.getString("title")
                if (jsonShow.has("poster")) {
                    movie.poster = jsonShow.getJSONObject("poster").getString("path")
                }
                movie.duration = jsonShow.optInt("runtime")
                if (jsonShow.has("statistics")) {
                    val jsonStatistics = jsonShow.getJSONObject("statistics")
                    movie.pressRating = jsonStatistics.optString("pressRating", "0")
                    movie.userRating = jsonStatistics.optString("userRating", "0")
                }
                if (jsonShow.has("movieCertificate")) {
                    val jsonCertificate = jsonShow.getJSONObject("movieCertificate").getJSONObject("certificate")
                    movie.certificate = jsonCertificate.getInt("code")
                    movie.certificateString = jsonCertificate.optString("$", "")
                }
                if (jsonShow.has("castingShort")) {
                    val jsonCasting = jsonShow.getJSONObject("castingShort")
                    movie.directors = jsonCasting.optString("directors", "")
                    movie.actors = jsonCasting.optString("actors", "")
                }
                if (jsonShow.has("genre")) {
                    val jsonGenres = jsonShow.getJSONArray("genre")
                    movie.genres = jsonGenres.getJSONObject(0).getString("$").toLowerCase(Locale.FRANCE)
                    for (j in 1 until jsonGenres.length()) {
                        movie.genres += ", " + jsonGenres.getJSONObject(j).getString("$").toLowerCase(Locale.FRANCE)
                    }
                }
                if (jsonShow.has("trailer")) {
                    val jsonTrailer = jsonShow.getJSONObject("trailer")
                    movie.trailerCode = jsonTrailer.optString("code", "")
                }
                val display = Display()
                try {
                    display.display = jsonMovie.getString("display")
                } catch (e: JSONException) {
                    // This movie is not displayed this week, skip.
                    continue
                }
                display.isOriginalLanguage = jsonMovie.getJSONObject("version").getString("original") == "true"
                if (jsonMovie.has("screenFormat") && jsonMovie.getJSONObject("screenFormat").has("$")) {
                    display.is3D = jsonMovie.getJSONObject("screenFormat").getString("$").contains("3D")
                    display.isIMAX = jsonMovie.getJSONObject("screenFormat").getString("$").contains("IMAX")
                }
                if (jsonMovie.has("screen") && jsonMovie.getJSONObject("screen").has("$")) {
                    display.screen = jsonMovie.getJSONObject("screen").getString("$")
                }
                if (jsonMovie.has("theater")) {
                    // displaying unified view, need to remind the display of
                    // the theater.
                    display.theater = jsonMovie.getString("theater")
                }
                movie.displays.add(display)
                moviesHash[code] = movie
            } catch (e: JSONException) {
                throw RuntimeException("An error occured while loading datas for " + theaterCode + ": " + e.message)
            }
        }

        // Build final ArrayList, to be used in adapter
        val resultsList = ArrayList(moviesHash.values)

        // Sort displays
        for (movie in resultsList) {
            Collections.sort(movie!!.displays, Collections.reverseOrder<Any>())
        }

        // Sort movies by rating
        Collections.sort(resultsList, Collections.reverseOrder<Any>())
        return resultsList
    }

    fun findMovie(movie: Movie): Movie {
        val jsonMovie = movie.code?.let { downloadMovie(it) }
        if (jsonMovie != null) {
            movie.synopsis = jsonMovie.optString("synopsisShort", "")
        }
        return movie
    }

    fun downloadTrailerUrl(movie: Movie): String? {
        if (movie.trailerCode == "") return null
        val params: MutableMap<String, String> = HashMap()
        params["mediafmt"] = "mp4-lc"
        params["code"] = movie.trailerCode
        return try {
            val json = downloadUrl("media", params, "https://gist.githubusercontent.com/Neamar/9713818694c4c37f583c4d5cf4046611/raw/cinemas-search.json")
            val jsonTrailer = JSONObject(json).getJSONObject("media")
            if (jsonTrailer.has("rendition")) jsonTrailer.getJSONArray("rendition").getJSONObject(0).getString("href") else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    companion object {
        private const val TAG = "APIHelper"
        private const val PARTNER_KEY = "100ED" + "1DA33EB"
        private const val SECRET_KEY = "1a1ed8c1bed24d60" + "ae3472eed1da33eb"
        private const val BASE_URL = "https://api.allocine.fr/rest/v3/"
    }
}