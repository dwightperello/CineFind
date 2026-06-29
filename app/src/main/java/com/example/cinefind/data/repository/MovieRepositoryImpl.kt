package com.example.cinefind.data.repository

import com.example.cinefind.data.model.MovieState
import com.example.cinefind.data.service.MovieApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MovieRepositoryImpl @Inject constructor(
    private val apiService: MovieApiService
) : MovieRepository {

    override suspend fun getMovie(id: Int): MovieState {
        return try {
            val response = withContext(Dispatchers.IO) { apiService.getMovie(id) }
            MovieState.Success(response)
        } catch (e: Exception) {
            MovieState.Error(e.message)
        }
    }
}
