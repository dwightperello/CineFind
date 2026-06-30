package com.example.cinefind.data.model

sealed class GenreState {
    data class Success(val genres: List<Genre>) : GenreState()
    data class Error(val message: String?) : GenreState()
}
