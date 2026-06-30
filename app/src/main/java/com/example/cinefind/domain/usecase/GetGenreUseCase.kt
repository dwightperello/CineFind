package com.example.cinefind.domain.usecase

import com.example.cinefind.data.model.GenreState
import com.example.cinefind.data.repository.MovieRepository
import javax.inject.Inject

class GetGenreUseCase @Inject constructor(
    private val repository: MovieRepository
) : BaseUseCaseNoParams<GenreState>() {

    override suspend fun invoke(): GenreState =
        repository.getGenre()
}