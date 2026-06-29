package com.example.cinefind.data.service

import com.example.cinefind.data.model.MovieResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieApiService {

    @GET("movie/{id}")
    suspend fun getMovie(
        @Path("id") id: Int,
        @Query("append_to_response") appendToResponse: String = "videos"
    ): MovieResponse
}
