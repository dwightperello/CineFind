package com.example.cinefind.presentation.feature.movie

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.cinefind.data.model.MovieState
import com.example.cinefind.domain.usecase.GetMovieUseCase
import com.example.cinefind.presentation.base.BaseViewModel
import com.example.cinefind.presentation.base.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MovieViewModel @Inject constructor(
    private val getMovieUseCase: GetMovieUseCase
) : BaseViewModel() {

    private val _movie = SingleLiveEvent<MovieState.Success>()
    val movie: LiveData<MovieState.Success> = _movie

    fun loadMovie(id: Int) {
        viewModelScope.launchSafely(
            execute = { getMovieUseCase.execute(id) },
            onSuccess = { state ->
                when (state) {
                    is MovieState.Success -> _movie.value = state
                    is MovieState.Error -> _genericError.value = state.message
                }
            }
        )
    }
}
