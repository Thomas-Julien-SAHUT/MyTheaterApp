package com.example.mytheater.services

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mytheater.R
import com.example.mytheater.models.Movie
import com.example.mytheater.viewmodels.MyMovieViewModel

class MyMoviesAdapter(
    private val myFavoriteMovies : List<MyMovieViewModel>,
    private val listener: OnItemClickListener)
    : RecyclerView.Adapter<MyMoviesAdapter.MyMoviesViewHolder>() {

    inner class MyMoviesViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView),View.OnClickListener{
        val imgMyMovie : ImageView = itemView.findViewById(R.id.imgMovieItem)
        val txtName : TextView = itemView.findViewById(R.id.txtMovieItemName)
        val txtDate : TextView = itemView.findViewById(R.id.txtMovieItemDate)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if(position != RecyclerView.NO_POSITION){
                listener.onItemCLick(position)
            }
        }
    }

    interface OnItemClickListener{
        fun onItemCLick(position: Int){

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyMoviesViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.movie_item, parent, false)

        return MyMoviesViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyMoviesViewHolder, position: Int) {

        val currentItem = myFavoriteMovies[position]
        holder.imgMyMovie.setImageResource(currentItem.imageResource)
        holder.txtName.setText(currentItem.name)
        holder.txtDate.setText(currentItem.date)
    }

    override fun getItemCount(): Int = myFavoriteMovies.size

}
