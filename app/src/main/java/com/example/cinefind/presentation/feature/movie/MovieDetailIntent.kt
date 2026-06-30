package com.example.cinefind.presentation.feature.movie

sealed class MovieDetailIntent {
    data class LoadMovie(val id: Int) : MovieDetailIntent()
}
