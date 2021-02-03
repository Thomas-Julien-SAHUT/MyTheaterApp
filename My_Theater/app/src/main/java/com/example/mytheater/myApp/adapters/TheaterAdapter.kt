package com.example.mytheater.myApp.adapters

import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import com.example.mytheater.R
import com.example.mytheater.myApp.db.DBHelper
import com.example.mytheater.myApp.objects.Theater
import java.text.DecimalFormat
import java.util.*

class TheaterAdapter(context: Context?, textViewResourceId: Int, theaters: ArrayList<Theater>) : ArrayAdapter<Theater>(
    context!!, textViewResourceId, ((theaters as List<Theater>)!!)
) {
    private val inflater: LayoutInflater = LayoutInflater.from(context)

    /**
     * Array list containing all the theaters currently displayed
     */
    @JvmField
    var theaters = ArrayList<Theater>()
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var v = convertView
        if (v == null) {
            v = inflater.inflate(R.layout.item_theater, null)
        }
        val theater = theaters[position]
        val theaterName = v!!.findViewById<View>(R.id.listitem_theater_name) as TextView
        theaterName.text = theater.title
        val theaterLocation = v.findViewById<View>(R.id.listitem_theater_location) as TextView
        if (theater.distance != -1.0) {
            val distance: String
            distance = if (theater.distance < 1) {
                val dist = Math.round(theater.distance * 1000)
                dist.toString() + "m"
            } else {
                DecimalFormat("#.#").format(theater.distance).toString() + "km"
            }
            theaterLocation.text = Html.fromHtml("<b>" + distance + "</b> - " + theater.location)
        } else {
            if (theater.city == "") {
                theaterLocation.text = theater.location
            } else {
                theaterLocation.text = String.format("%s – %s", theater.location, theater.city)
            }
        }
        val fav = v.findViewById<View>(R.id.listitem_theater_fav) as CheckBox
        fav.isChecked = theater.code?.let { DBHelper.isFavorite(it) } == true
        fav.setOnClickListener { v ->
            if (fav.isChecked) {
                theater.code?.let { DBHelper.insertFavorite(v.context, it, theater.title, theater.location, theater.city) }
                Toast.makeText(context, "Cinéma ajouté aux favoris", Toast.LENGTH_SHORT).show()
            } else {
                theater.code?.let { DBHelper.removeFavorite(v.context, it) }
                Toast.makeText(context, "Cinéma retiré des favoris", Toast.LENGTH_SHORT).show()
            }
        }
        return v
    }

    init {
        this.theaters = theaters
    }
}