package com.example.cinefind.data.model

sealed class MovieState {
    data class Success(val movie: MovieResponse) : MovieState()
    data class Error(val message: String?) : MovieState()
}
