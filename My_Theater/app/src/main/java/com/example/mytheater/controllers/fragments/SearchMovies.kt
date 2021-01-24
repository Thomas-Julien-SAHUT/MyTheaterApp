package com.example.mytheater.controllers.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mytheater.R
import com.example.mytheater.models.Movie
import com.example.mytheater.services.SearchMoviesAdapter
import java.util.*
import kotlin.collections.ArrayList

class SearchMovies :  Fragment(), SearchMoviesAdapter.OnItemClickListener {

    private lateinit var movieList : List<Movie>
    private lateinit var adapter : SearchMoviesAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_my_movies, container, false)

        movieList = generateDummyList(20)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerMyMovies)
        this.adapter = SearchMoviesAdapter(movieList,this)
        recyclerView.adapter = this.adapter
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        recyclerView.setHasFixedSize(true)

        return view
    }

    override fun onItemCLick(position: Int) {
        val clickedItem = movieList[position]
        Toast.makeText(this.context, "Item " + clickedItem.name + "clicked", Toast.LENGTH_SHORT).show()
        adapter.notifyItemChanged(position)
    }

    private fun generateDummyList(size :Int): List<Movie>{
        val list = ArrayList<Movie>()
        for (i in 1 until size){
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
