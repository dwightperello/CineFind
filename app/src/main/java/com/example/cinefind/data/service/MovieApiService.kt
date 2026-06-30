package com.example.cinefind.data.service

import com.example.cinefind.data.model.AccountResponse
import com.example.cinefind.data.model.FavoriteRequest
import com.example.cinefind.data.model.FavoriteResponse
import com.example.cinefind.data.model.GenreResponse
import com.example.cinefind.data.model.MovieResponse
import com.example.cinefind.data.model.MoviesBasedOnGenresResponse
import com.example.cinefind.data.model.UpcomingMovieResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieApiService {

    @GET("movie/{id}")
    suspend fun getMovie(
        @Path("id") id: Int,
        @Query("append_to_response") appendToResponse: String = "videos"
    ): MovieResponse

    @GET("movie/upcoming")
    suspend fun getUpcomingMovies(): UpcomingMovieResponse

    @GET("genre/movie/list")
    suspend fun getGenre(): GenreResponse

    @GET("account")
    suspend fun getAccount(): AccountResponse

    @POST("account/{accountId}/favorite")
    suspend fun markFavorite(
        @Path("accountId") accountId: Int,
        @Body body: FavoriteRequest
    ): FavoriteResponse

    @GET(value = "discover/movie")
    suspend fun getMoviesBasedGenre(
        @Query("with_genres") genreId: Int
    ): MoviesBasedOnGenresResponse
}
