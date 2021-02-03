package com.example.mytheater.myApp

import android.app.ListActivity
import android.content.Intent
import android.content.Intent.ShortcutIconResource
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import com.example.mytheater.R
import com.example.mytheater.myApp.db.DBHelper.getFavorites
import com.example.mytheater.myApp.objects.Theater
import java.util.*

class ShortcutActivity : ListActivity() {
    private var theaters: ArrayList<Theater>? = null
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        theaters = getFavorites(this)
        if (theaters!!.size > 0) {
            listAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, theaters!!)
        } else {
            Toast.makeText(
                this,
                "Ajoutez un cinéma aux favoris pour l'ajouter directement à l'écran d'accueil.",
                Toast.LENGTH_LONG
            ).show()
            finish()
        }
    }

    override fun onListItemClick(l: ListView, v: View, position: Int, id: Long) {
        // The meat of our shortcut
        val shortcutIntent = Intent(this, MoviesActivity::class.java)
        shortcutIntent.putExtra("code", theaters!![position].code)
        shortcutIntent.putExtra("theater", theaters!![position].title)
        val iconResource = ShortcutIconResource.fromContext(this, R.drawable.ic_launcher)

        // The result we are passing back from this activity
        val intent = Intent()
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent)
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, theaters!![position].title)
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconResource)
        setResult(RESULT_OK, intent)
        finish()
    }
}