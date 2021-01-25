package com.example.mytheater.controllers.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mytheater.R
import com.example.mytheater.controllers.stub.Stub
import com.example.mytheater.services.MyMoviesAdapter
import com.example.mytheater.viewmodels.MyMovieViewModel

class MyMovies : Fragment(), MyMoviesAdapter.OnItemClickListener {

    private lateinit var movieList : List<MyMovieViewModel>
    private lateinit var adapter : MyMoviesAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_my_movies, container, false)

        movieList = Stub.generateMyDummyList(20)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerMyMovies)
        adapter = MyMoviesAdapter(movieList,this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        recyclerView.setHasFixedSize(true)

        return view
    }

    override fun onItemCLick(position: Int) {
        val clickedItem = movieList[position]
        Toast.makeText(this.context, clickedItem.name + "clicked", Toast.LENGTH_SHORT).show()
        adapter.notifyItemChanged(position)
    }

}
