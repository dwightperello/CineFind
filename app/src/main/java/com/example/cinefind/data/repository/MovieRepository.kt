package com.example.cinefind.data.repository

import com.example.cinefind.data.model.AccountResponse
import com.example.cinefind.data.model.FavoriteResponse
import com.example.cinefind.data.model.GenreState
import com.example.cinefind.data.model.MovieState
import com.example.cinefind.data.model.MyFavoritesResponse

interface MovieRepository {
    suspend fun getMovie(id: Int): MovieState
    suspend fun getUpcomingMovies(): MovieState
    suspend fun getGenre(): GenreState
    suspend fun getAccount(): AccountResponse
    suspend fun markFavorite(accountId: Int, movieId: Int, favorite: Boolean): FavoriteResponse

    suspend fun getMyFavorites(accountId: Int): MovieState

    suspend fun getMoviesBasedGenre(genreId: Int) : MovieState
}
