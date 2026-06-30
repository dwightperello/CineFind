package com.example.cinefind.data.model

sealed class MovieDetailState {
    object Idle : MovieDetailState()
    object Loading : MovieDetailState()
    data class Success(val movie: MovieResponse) : MovieDetailState()
    data class Error(val message: String?) : MovieDetailState()
}
