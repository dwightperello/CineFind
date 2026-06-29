package com.example.cinefind.data.model

import com.google.gson.annotations.SerializedName

data class MovieResponse(
    val id: Int?,
    val title: String?,
    val overview: String?,
    @SerializedName("release_date") val releaseDate: String?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("vote_average") val voteAverage: Double?,
    val status: String?
)
