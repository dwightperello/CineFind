package com.example.cinefind.data.service

import com.example.cinefind.data.model.GenreResponse
import com.example.cinefind.data.model.MovieResponse
import com.example.cinefind.data.model.UpcomingMovieResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieApiService {

    @GET("movie/{id}")
    suspend fun getMovie(
        @Path("id") id: Int,
        @Query("append_to_response") appendToResponse: String = "videos"
    ): MovieResponse

    @GET(value = "movie/upcoming")
    suspend fun getUpcomingMovies(): UpcomingMovieResponse

    @GET("genre/movie/list")
    suspend fun getGenre(): GenreResponse
}
