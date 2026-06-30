package com.example.cinefind.presentation.feature.movie

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cinefind.R
import com.example.cinefind.databinding.FragmentMovieListBinding
import com.example.cinefind.presentation.adapter.GenreAdapter
import com.example.cinefind.presentation.adapter.MovieAdapter

class MovieListFragment : Fragment() {

    private var _binding: FragmentMovieListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MovieViewModel by activityViewModels()

    private lateinit var genreAdapter: GenreAdapter
    private lateinit var movieAdapter: MovieAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMovieListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupGenreRecyclerView()
        setupMovieRecyclerView()
        viewModel.loadAccount()
        viewModel.loadUpcomingMovies()
        viewModel.loadGenres()
        observeData()
    }

    private fun setupGenreRecyclerView() {
        genreAdapter = GenreAdapter { genre ->
            Log.d("mapping", "Selected genre: ${genre.name} (id=${genre.id})")
            genre.id?.let { id ->
                viewModel.getMoviesBasedOnGenre(id)
            }
        }
        binding.rvGenres.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = genreAdapter
        }
    }

    private fun setupMovieRecyclerView() {
        movieAdapter = MovieAdapter(
            onLikeClick = { movie, isFavorite ->
                movie.id?.let { viewModel.markFavorite(it, isFavorite) }
            },
            onCardClick = { movie ->
                movie.id?.let { id ->
                    viewModel.processIntent(MovieDetailIntent.LoadMovie(id))
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, MovieDetailFragment())
                        .addToBackStack(null)
                        .commit()
                }
            }
        )
        binding.rvMovies.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = movieAdapter
        }
    }

    private fun observeData() {
        viewModel.movieUpcoming.observe(viewLifecycleOwner) { state ->
            movieAdapter.submitList(state.movies)
        }
        viewModel.genre.observe(viewLifecycleOwner) { state ->
            genreAdapter.submitList(state.genres)
        }
        viewModel.genreMovies.observe(viewLifecycleOwner) { state ->
            movieAdapter.submitList(state.movies)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
