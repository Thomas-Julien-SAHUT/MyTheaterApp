package com.example.mytheater.services

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mytheater.R
import com.example.mytheater.models.Movie

class MyMoviesAdapter(private val myFavoriteMovies : List<Movie>) : RecyclerView.Adapter<MyMoviesAdapter.MyMoviesViewHolder>() {

    class MyMoviesViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView){
        val imgMyMovie : ImageView = itemView.findViewById(R.id.imgMyMovieItem)
        val txtName : TextView = itemView.findViewById(R.id.txtMyMovieItemName)
        val txtDate : TextView = itemView.findViewById(R.id.txtMyMovieItemDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyMoviesViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.movie_item, parent, false)

        return MyMoviesViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyMoviesViewHolder, position: Int) {
        val currentItem = myFavoriteMovies[position]
        holder.imgMyMovie.setImageResource(currentItem.imageRessource)
        holder.txtName.setText(currentItem.name)
        holder.txtDate.setText(currentItem.date)
    }

    override fun getItemCount(): Int = myFavoriteMovies.size

}