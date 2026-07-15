package com.example.cinefind.presentation.adapter

import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.recyclerview.widget.RecyclerView
import com.example.cinefind.data.model.MovieResponse
import com.example.mvvmcomposebase.MovieCard

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

    fun markAllFavorite(ids: Set<Int>) {
        likedIds.clear()
        likedIds.addAll(ids)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val composeView = ComposeView(parent.context).apply {
            layoutParams = RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT
            )
        }
        return MovieViewHolder(composeView)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(movies[position])
    }

    override fun getItemCount() = movies.size

    inner class MovieViewHolder(private val composeView: ComposeView) :
        RecyclerView.ViewHolder(composeView) {

        fun bind(movie: MovieResponse) {
            val isLiked = movie.id != null && likedIds.contains(movie.id)

            composeView.setContent {
                MovieCard(
                    title = movie.title ?: movie.originalTitle.orEmpty(),
                    posterUrl = movie.posterPath?.let { "https://image.tmdb.org/t/p/w342$it" },
                    isLiked = isLiked,
                    onCardClick = { onCardClick(movie) },
                    onLikeClick = {
                        val pos = bindingAdapterPosition
                        val id = movie.id
                        if (pos != RecyclerView.NO_POSITION && id != null) {
                            if (likedIds.contains(id)) likedIds.remove(id) else likedIds.add(id)
                            notifyItemChanged(pos)
                            onLikeClick(movie, likedIds.contains(id))
                        }
                    }
                )
            }
        }
    }
}
