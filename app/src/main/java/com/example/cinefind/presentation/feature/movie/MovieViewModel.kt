package com.example.cinefind.presentation.feature.movie

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.cinefind.data.model.GenreState
import com.example.cinefind.data.model.MovieDetailState
import com.example.cinefind.data.model.MovieState
import com.example.cinefind.domain.usecase.GetAccountUseCase
import com.example.cinefind.domain.usecase.GetGenreUseCase
import com.example.cinefind.domain.usecase.GetMovieUseCase
import com.example.cinefind.domain.usecase.GetMoviesBasedOnGenreUseCase
import com.example.cinefind.domain.usecase.GetMyFavoriteMoviesUseCase
import com.example.cinefind.domain.usecase.GetUpcomingMovieUseCase
import com.example.cinefind.domain.usecase.MarkFavoriteUseCase
import com.example.mvvmbase.BaseViewModel
import com.example.mvvmbase.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MovieViewModel @Inject constructor(
    private val getMovieUseCase: GetMovieUseCase,
    private val getUpcomingMovieUseCase: GetUpcomingMovieUseCase,
    private val getGenreUseCase: GetGenreUseCase,
    private val getAccountUseCase: GetAccountUseCase,
    private val markFavoriteUseCase: MarkFavoriteUseCase,
    private val getMoviesBasedOnGenreUseCase: GetMoviesBasedOnGenreUseCase,
    private val getMyFavoriteMoviesUseCase: GetMyFavoriteMoviesUseCase
) : BaseViewModel() {

    private val _movieDetailState = MutableStateFlow<MovieDetailState>(MovieDetailState.Idle)
    val movieDetailState: StateFlow<MovieDetailState> = _movieDetailState.asStateFlow()

    private val _movieUpcoming = SingleLiveEvent<MovieState.SuccessList>()
    val movieUpcoming: LiveData<MovieState.SuccessList> = _movieUpcoming

    private val _myFavoriteMovie = SingleLiveEvent<MovieState.SuccessList>()
    val myFavoriteMovie: LiveData<MovieState.SuccessList> = _myFavoriteMovie

    private val _genre = SingleLiveEvent<GenreState.Success>()
    val genre: LiveData<GenreState.Success> = _genre

    private val _genreMovies = SingleLiveEvent<MovieState.SuccessList>()
    val genreMovies : LiveData<MovieState.SuccessList> = _genreMovies

    private var accountId: Int? = null

    fun processIntent(intent: MovieDetailIntent) {
        when (intent) {
            is MovieDetailIntent.LoadMovie -> fetchMovieDetail(intent.id)
        }
    }

    private fun fetchMovieDetail(id: Int) {
        _movieDetailState.value = MovieDetailState.Loading
        viewModelScope.launchSafely(
            execute = { getMovieUseCase.execute(id) },
            onSuccess = { state ->
                when (state) {
                    is MovieState.Success -> _movieDetailState.value = MovieDetailState.Success(state.movie)
                    is MovieState.Error -> _movieDetailState.value = MovieDetailState.Error(state.message)
                    else -> Unit
                }
            }
        )
    }

    fun getMoviesBasedOnGenre(id: Int){
        viewModelScope.launchSafely(
            execute = {getMoviesBasedOnGenreUseCase.execute(id)},
            onSuccess = {state ->
                when(state){
                    is MovieState.SuccessList -> _genreMovies.value = state
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
                when (state) {
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
                when (state) {
                    is GenreState.Success -> _genre.value = state
                    is GenreState.Error -> _genericError.value = state.message
                }
            }
        )
    }

    fun loadAccount() {
        if (accountId != null) return
        viewModelScope.launchSafely(
            execute = { getAccountUseCase.execute() },
            onSuccess = { response -> accountId = response.id }
        )
    }

    fun getMyFavoriteMovies(){
        val id = accountId ?: run {
            Log.w("mapping", "markFavorite called before account loaded")
            return
        }

        viewModelScope.launchSafely(
            execute = {getMyFavoriteMoviesUseCase.execute(id)},
            onSuccess = { state ->
                when (state) {
                    is MovieState.SuccessList -> _myFavoriteMovie.value = state
                    is MovieState.Error -> _genericError.value = state.message
                    else -> Unit
                }
            }
        )
    }

    fun markFavorite(movieId: Int, isFavorite: Boolean) {
        val id = accountId ?: run {
            Log.w("mapping", "markFavorite called before account loaded")
            return
        }
        viewModelScope.launchSafely(
            execute = { markFavoriteUseCase.execute(MarkFavoriteUseCase.Params(id, movieId, isFavorite)) },
            onSuccess = { response ->
                Log.d("mapping", "movieId=$movieId favorite=$isFavorite → ${response.statusMessage}")
            },
            onError = { error ->
                Log.e("mapping", "markFavorite failed: ${error.message}")
                _genericError.value = error.message
            }
        )
    }
}
