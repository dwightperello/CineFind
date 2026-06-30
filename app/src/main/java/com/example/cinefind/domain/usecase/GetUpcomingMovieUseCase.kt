package com.example.cinefind.domain.usecase

import com.example.cinefind.data.model.MovieState
import com.example.cinefind.data.repository.MovieRepository
import javax.inject.Inject

class GetUpcomingMovieUseCase @Inject constructor(
    private val repository: MovieRepository
) : BaseUseCaseNoParams<MovieState>() {

    override suspend fun invoke(): MovieState =
        repository.getUpcomingMovies()
}