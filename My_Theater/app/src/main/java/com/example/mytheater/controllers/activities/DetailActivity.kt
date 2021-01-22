package com.example.mytheater.controllers.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import com.example.mytheater.R

class DetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        configureToolBar()
    }
    fun configureToolBar(){
        val toolbar = findViewById<Toolbar>(R.id.toolbar_main)
        setSupportActionBar(toolbar)
        val ab : ActionBar? = supportActionBar
        ab?.setDisplayHomeAsUpEnabled(true)
    }
}