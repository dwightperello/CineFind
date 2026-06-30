package com.example.cinefind.data.repository

import com.example.cinefind.data.model.AccountResponse
import com.example.cinefind.data.model.FavoriteRequest
import com.example.cinefind.data.model.GenreState
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

    override suspend fun getUpcomingMovies(): MovieState {
        return try {
            val response = withContext(Dispatchers.IO) { apiService.getUpcomingMovies() }
            MovieState.SuccessList(response.results)
        } catch (e: Exception) {
            MovieState.Error(e.message)
        }
    }

    override suspend fun getGenre(): GenreState {
        return try {
            val response = withContext(Dispatchers.IO) { apiService.getGenre() }
            GenreState.Success(response.genres)
        } catch (e: Exception) {
            GenreState.Error(e.message)
        }
    }

    override suspend fun getAccount(): AccountResponse =
        withContext(Dispatchers.IO) { apiService.getAccount() }

    override suspend fun markFavorite(accountId: Int, movieId: Int, favorite: Boolean) =
        withContext(Dispatchers.IO) {
            apiService.markFavorite(accountId, FavoriteRequest(mediaId = movieId, favorite = favorite))
        }

    override suspend fun getMoviesBasedGenre(genreId: Int): MovieState {
        return try {
            val response = withContext(Dispatchers.IO) { apiService.getMoviesBasedGenre(genreId) }
            MovieState.SuccessList(response.results)
        } catch (e: Exception) {
            MovieState.Error(e.message)
        }
    }
}
