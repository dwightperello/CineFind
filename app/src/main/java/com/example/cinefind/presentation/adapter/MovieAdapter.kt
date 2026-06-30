package com.example.cinefind.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.cinefind.R
import com.example.cinefind.data.model.MovieResponse
import com.example.cinefind.databinding.ItemMovieCardBinding

class MovieAdapter(
    private val onLikeClick: (MovieResponse, Boolean) -> Unit,
    private val onCardClick: (MovieResponse) -> Unit
) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    private val movies = mutableListOf<MovieResponse>()
    private val likedIds = mutableSetOf<Int>()

    fun submitList(list: List<MovieResponse>) {
        movies.clear()
        movies.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = ItemMovieCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(movies[position])
    }

    override fun getItemCount() = movies.size

    inner class MovieViewHolder(private val binding: ItemMovieCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: MovieResponse) {
            binding.tvMovieTitle.text = movie.title ?: movie.originalTitle

            binding.ivPoster.load("https://image.tmdb.org/t/p/w342${movie.posterPath}") {
                crossfade(true)
                placeholder(android.R.color.darker_gray)
            }

            val isLiked = movie.id != null && likedIds.contains(movie.id)
            binding.btnLike.setImageResource(
                if (isLiked) R.drawable.ic_heart_filled else R.drawable.ic_heart_outline
            )

            binding.root.setOnClickListener {
                onCardClick(movie)
            }

            binding.btnLike.setOnClickListener {
                val pos = bindingAdapterPosition
                if (pos == RecyclerView.NO_POSITION) return@setOnClickListener
                val id = movie.id ?: return@setOnClickListener
                if (likedIds.contains(id)) likedIds.remove(id) else likedIds.add(id)
                notifyItemChanged(pos)
                onLikeClick(movie, likedIds.contains(id))
            }
        }
    }
}
