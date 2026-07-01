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

class GetUpcomingMovieUseCaseTest {

    private val repository: MovieRepository = mockk()

    private lateinit var useCase: GetUpcomingMovieUseCase

    @Before
    fun setUp() {
        useCase = GetUpcomingMovieUseCase(repository)
    }

    // ----- Test 1: happy path -----------------------------------------------

    @Test
    fun `execute returns SuccessList when repository returns upcoming movies`() = runTest {

        // --- Arrange ---
        val fakeMovies = listOf(
            MovieResponse(
                id = 101,
                title = "Dune: Part Three",
                overview = "The saga continues on Arrakis.",
                releaseDate = "2026-11-20",
                posterPath = "/dune3_poster.jpg",
                voteAverage = 8.5,
                originalTitle = "Dune: Part Three",
                status = "Upcoming"
            ),
            MovieResponse(
                id = 102,
                title = "Avatar 3",
                overview = "Jake Sully returns to Pandora once more.",
                releaseDate = "2026-12-19",
                posterPath = "/avatar3_poster.jpg",
                voteAverage = 7.9,
                originalTitle = "Avatar 3",
                status = "Upcoming"
            )
        )
        val expectedState = MovieState.SuccessList(fakeMovies)

        coEvery { repository.getUpcomingMovies() } returns expectedState

        // --- Act ---
        val result = useCase.execute()

        // --- Assert ---
        assertEquals(expectedState, result)
    }

    // ----- Test 2: empty list -----------------------------------------------

    @Test
    fun `execute returns SuccessList with empty list when no upcoming movies`() = runTest {

        // --- Arrange ---
        val expectedState = MovieState.SuccessList(emptyList())

        coEvery { repository.getUpcomingMovies() } returns expectedState

        // --- Act ---
        val result = useCase.execute()

        // --- Assert ---
        assertEquals(expectedState, result)
    }

    // ----- Test 3: error path -----------------------------------------------

    @Test
    fun `execute returns Error when repository fails`() = runTest {

        // --- Arrange ---
        val expectedState = MovieState.Error("Failed to fetch upcoming movies")

        coEvery { repository.getUpcomingMovies() } returns expectedState

        // --- Act ---
        val result = useCase.execute()

        // --- Assert ---
        assertEquals(expectedState, result)
    }
}
