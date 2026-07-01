package com.example.cinefind.presentation.feature.movie

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.cinefind.data.model.MovieDetailState
import com.example.cinefind.data.model.MovieResponse
import com.example.cinefind.data.model.MovieState
import com.example.cinefind.domain.usecase.GetAccountUseCase
import com.example.cinefind.domain.usecase.GetGenreUseCase
import com.example.cinefind.domain.usecase.GetMovieUseCase
import com.example.cinefind.domain.usecase.GetMoviesBasedOnGenreUseCase
import com.example.cinefind.domain.usecase.GetUpcomingMovieUseCase
import com.example.cinefind.domain.usecase.MarkFavoriteUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

// ---------------------------------------------------------------------------
// HOW TO READ THIS FILE
// ---------------------------------------------------------------------------
// The ViewModel uses viewModelScope (which normally runs on the Main dispatcher,
// i.e. the Android UI thread).  In a unit test there is no Android runtime, so
// we swap the Main dispatcher for a test-friendly one that runs coroutines
// immediately and synchronously.
//
// We also need InstantTaskExecutorRule so that LiveData posts happen on the
// same thread instead of a background thread — otherwise our assertions would
// run before the value is set.
// ---------------------------------------------------------------------------

@OptIn(ExperimentalCoroutinesApi::class)
class MovieViewModelTest {

    // Rule 1: Makes LiveData.setValue / postValue work synchronously in tests.
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    // All 6 use cases the ViewModel needs — we mock them all even if a test
    // only exercises one.  The others simply won't be called.
    private val getMovieUseCase: GetMovieUseCase = mockk()
    private val getUpcomingMovieUseCase: GetUpcomingMovieUseCase = mockk()
    private val getGenreUseCase: GetGenreUseCase = mockk()
    private val getAccountUseCase: GetAccountUseCase = mockk()
    private val markFavoriteUseCase: MarkFavoriteUseCase = mockk()
    private val getMoviesBasedOnGenreUseCase: GetMoviesBasedOnGenreUseCase = mockk()

    private lateinit var viewModel: MovieViewModel

    // UnconfinedTestDispatcher runs every coroutine eagerly (it doesn't queue
    // them) — this means by the time we reach our assertion the coroutine has
    // already finished.
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        // Replace the real Main dispatcher with our test one.
        Dispatchers.setMain(testDispatcher)

        viewModel = MovieViewModel(
            getMovieUseCase = getMovieUseCase,
            getUpcomingMovieUseCase = getUpcomingMovieUseCase,
            getGenreUseCase = getGenreUseCase,
            getAccountUseCase = getAccountUseCase,
            markFavoriteUseCase = markFavoriteUseCase,
            getMoviesBasedOnGenreUseCase = getMoviesBasedOnGenreUseCase
        )
    }

    @After
    fun tearDown() {
        // Always restore the real dispatcher when the test is done.
        Dispatchers.resetMain()
    }

    // ----- Test 1: success --------------------------------------------------

    @Test
    fun `processIntent LoadMovie emits Success state when use case returns a movie`() = runTest {

        // --- Arrange ---
        val fakeMovie = MovieResponse(
            id = 42,
            title = "The Dark Knight",
            overview = "Batman vs Joker.",
            releaseDate = "2008-07-18",
            posterPath = "/dark_knight.jpg",
            voteAverage = 9.0,
            originalTitle = "The Dark Knight",
            status = "Released"
        )
        coEvery { getMovieUseCase.execute(42) } returns MovieState.Success(fakeMovie)

        // --- Act ---
        viewModel.processIntent(MovieDetailIntent.LoadMovie(id = 42))

        // --- Assert ---
        // movieDetailState is a StateFlow — we can read its current value directly.
        val state = viewModel.movieDetailState.value
        assertEquals(MovieDetailState.Success(fakeMovie), state)
    }

    // ----- Test 2: error path -----------------------------------------------

    @Test
    fun `processIntent LoadMovie emits Error state when use case returns an error`() = runTest {

        // --- Arrange ---
        coEvery { getMovieUseCase.execute(99) } returns MovieState.Error("Not found")

        // --- Act ---
        viewModel.processIntent(MovieDetailIntent.LoadMovie(id = 99))

        // --- Assert ---
        val state = viewModel.movieDetailState.value
        assertEquals(MovieDetailState.Error("Not found"), state)
    }
}
