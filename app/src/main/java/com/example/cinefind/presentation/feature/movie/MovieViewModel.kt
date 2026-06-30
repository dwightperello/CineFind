package com.example.cinefind.presentation.feature.movie

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.cinefind.data.model.GenreState
import com.example.cinefind.data.model.MovieState
import com.example.cinefind.domain.usecase.GetGenreUseCase
import com.example.cinefind.domain.usecase.GetMovieUseCase
import com.example.cinefind.domain.usecase.GetUpcomingMovieUseCase
import com.example.cinefind.presentation.base.BaseViewModel
import com.example.cinefind.presentation.base.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MovieViewModel @Inject constructor(
    private val getMovieUseCase: GetMovieUseCase,
    private val  getUpcomingMovieUseCase: GetUpcomingMovieUseCase,
    private val getGenreUseCase: GetGenreUseCase
) : BaseViewModel() {

    private val _movie = SingleLiveEvent<MovieState.Success>()
    val movie: LiveData<MovieState.Success> = _movie

    private val _movieUpcoming = SingleLiveEvent<MovieState.SuccessList>()
    val movieUpcoming: LiveData<MovieState.SuccessList> = _movieUpcoming

    private val _genre = SingleLiveEvent<GenreState.Success>()
    val genre : LiveData<GenreState.Success> = _genre

    fun loadMovie(id: Int) {
        viewModelScope.launchSafely(
            execute = { getMovieUseCase.execute(id) },
            onSuccess = { state ->
                when (state) {
                    is MovieState.Success -> _movie.value = state
                    is MovieState.Error -> _genericError.value = state.message
                    else -> Unit
                }
            }
        )
    }

    fun loadUpcomingMovies() {
        viewModelScope.launchSafely(
            execute = { getUpcomingMovieUseCase.execute() },
            onSuccess = { state ->
                when(state){
                    is MovieState.SuccessList -> _movieUpcoming.value = state
                    is MovieState.Error -> _genericError.value = state.message
                    else -> Unit
                }
            }
        )
    }

    fun loadGenres() {
        viewModelScope.launchSafely(
            execute = { getGenreUseCase.execute() },
            onSuccess = { state ->
                when(state){
                    is GenreState.Success -> _genre.value = state
                    is GenreState.Error -> _genericError.value = state.message
                }
            }
        )
    }
}
