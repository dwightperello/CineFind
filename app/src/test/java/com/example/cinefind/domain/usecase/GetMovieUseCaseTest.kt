package com.example.cinefind.domain.usecase

import com.example.cinefind.data.model.MovieResponse
import com.example.cinefind.data.model.MovieState
import com.example.cinefind.data.repository.MovieRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

// ---------------------------------------------------------------------------
// HOW TO READ THIS FILE
// ---------------------------------------------------------------------------
// Every test follows the "Arrange / Act / Assert" pattern:
//   Arrange — set up fake objects and tell them what to return
//   Act     — call the real code you want to test
//   Assert  — check the result matches what you expected
// ---------------------------------------------------------------------------

class GetMovieUseCaseTest {

    // `mockk<MovieRepository>()` creates a fake object that implements the
    // MovieRepository interface.  We control exactly what it returns in each
    // test so we never need a real server.
    private val repository: MovieRepository = mockk()

    // This is the real class we are testing.
    private lateinit var useCase: GetMovieUseCase

    // @Before runs once before every @Test method.
    @Before
    fun setUp() {
        useCase = GetMovieUseCase(repository)
    }

    // ----- Test 1: happy path -----------------------------------------------

    @Test
    fun `execute returns Success when repository returns a movie`() = runTest {
        // runTest lets us call suspend functions inside a normal JUnit test.

        // --- Arrange ---
        val fakeMovie = MovieResponse(
            id = 1,
            title = "Inception",
            overview = "A thief enters dreams.",
            releaseDate = "2010-07-16",
            posterPath = "/poster.jpg",
            voteAverage = 8.8,
            originalTitle = "Inception",
            status = "Released"
        )
        val expectedState = MovieState.Success(fakeMovie)

        // `coEvery` is the suspend-function version of "when this is called, return this".
        coEvery { repository.getMovie(id = 1) } returns expectedState

        // --- Act ---
        val result = useCase.execute(params = 1)

        // --- Assert ---
        assertEquals(expectedState, result)
    }

    // ----- Test 2: error path -----------------------------------------------

    @Test
    fun `execute returns Error when repository returns an error`() = runTest {

        // --- Arrange ---
        val expectedState = MovieState.Error("Movie not found")

        coEvery { repository.getMovie(id = 99) } returns expectedState

        // --- Act ---
        val result = useCase.execute(params = 99)

        // --- Assert ---
        assertEquals(expectedState, result)
    }
}
