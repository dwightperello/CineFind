package com.example.cinefind.domain.usecase

import com.example.cinefind.data.model.FavoriteResponse
import com.example.cinefind.data.repository.MovieRepository
import javax.inject.Inject

class MarkFavoriteUseCase @Inject constructor(
    private val repository: MovieRepository
) : BaseUseCase<FavoriteResponse, MarkFavoriteUseCase.Params>() {

    data class Params(val accountId: Int, val movieId: Int, val favorite: Boolean)

    override suspend fun invoke(params: Params): FavoriteResponse =
        repository.markFavorite(params.accountId, params.movieId, params.favorite)
}
