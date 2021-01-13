package com.example.mytheater.controllers.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mytheater.R
import com.example.mytheater.models.Movie
import com.example.mytheater.services.MyMoviesAdapter
import java.util.*
import kotlin.collections.ArrayList

class MyMovies : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_my_movies, container, false)

        val movieList = generateDummyList(10)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerMyMovies)
        recyclerView.adapter = MyMoviesAdapter(movieList)
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        recyclerView.setHasFixedSize(true)

        return view
    }

    private fun generateDummyList(size :Int): List<Movie>{
        val list = ArrayList<Movie>()
        for (i in 0 until size){
            val drawable = when (i % 3){
                0-> R.drawable.ic_baseline_local_movies_24
                1-> R.drawable.ic_baseline_movie_filter_24
                else-> R.drawable.ic_baseline_ondemand_video_24
            }

            val item = Movie(drawable,"Item $i", "Line ${Date()}")
            list+=item
        }
        return list
    }
}