package com.example.cinefind.data.model

import com.google.gson.annotations.SerializedName

data class FavoriteRequest(
    @SerializedName("media_type") val mediaType: String = "movie",
    @SerializedName("media_id") val mediaId: Int,
    val favorite: Boolean
)
