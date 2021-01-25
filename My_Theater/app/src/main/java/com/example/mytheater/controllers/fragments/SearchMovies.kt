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
import com.example.mytheater.controllers.stub.Stub
import com.example.mytheater.models.Movie
import com.example.mytheater.services.SearchMoviesAdapter
import com.example.mytheater.viewmodels.MovieViewModel
import java.util.*
import kotlin.collections.ArrayList

class SearchMovies :  Fragment(), SearchMoviesAdapter.OnItemClickListener {

    private lateinit var movieList : List<MovieViewModel>
    private lateinit var adapter : SearchMoviesAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_my_movies, container, false)

        movieList = Stub.generateDummyList(20)
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

}
