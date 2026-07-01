package com.example.cinefind.domain.usecase

import com.example.cinefind.data.model.MovieState
import com.example.cinefind.data.repository.MovieRepository
import javax.inject.Inject

class GetMyFavoriteMoviesUseCase @Inject constructor(
    private val repository: MovieRepository
): BaseUseCase<MovieState, Int>() {

    override suspend fun invoke(params: Int): MovieState =
        repository.getMyFavorites(params)
}