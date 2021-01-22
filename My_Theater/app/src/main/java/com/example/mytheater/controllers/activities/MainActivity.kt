package com.example.mytheater.controllers.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.example.mytheater.R


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        configureToolBar()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.menu_main_search ->{
                Toast.makeText(this, "Recherche indisponible, demandez plutôt l'avis de Google, c'est mieux et plus rapide.", Toast.LENGTH_LONG).show()
                launchDetailActivity()
                return true
            }
            else-> return super.onOptionsItemSelected(item);
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main,menu)
        return true
    }

    fun configureToolBar(){
        var toolbar = findViewById<Toolbar>(R.id.toolbar_main)
        setSupportActionBar(toolbar)
    }

    fun launchDetailActivity(){
        val _Intent  = Intent(this@MainActivity, DetailActivity::class.java)
        this.startActivity(_Intent)
    }
}