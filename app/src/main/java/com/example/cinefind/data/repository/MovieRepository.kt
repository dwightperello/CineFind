package com.example.cinefind.data.repository

import com.example.cinefind.data.model.MovieState

interface MovieRepository {
    suspend fun getMovie(id: Int): MovieState
}
