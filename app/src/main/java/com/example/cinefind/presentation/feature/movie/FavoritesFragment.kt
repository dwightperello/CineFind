package com.example.cinefind.presentation.feature.movie

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.cinefind.R
import com.example.cinefind.databinding.FragmentFavoritesBinding
import com.example.cinefind.presentation.adapter.MovieAdapter

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MovieViewModel by activityViewModels()
    private lateinit var movieAdapter: MovieAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeData()
        viewModel.getMyFavoriteMovies()
    }

    private fun setupRecyclerView() {
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
        binding.rvFavorites.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = movieAdapter
        }
    }

    private fun observeData() {
        viewModel.myFavoriteMovie.observe(viewLifecycleOwner) { state ->
            val movies = state.movies
            movieAdapter.submitList(movies)
            movieAdapter.markAllFavorite(movies.mapNotNull { it.id }.toSet())
            binding.tvEmpty.visibility = if (movies.isEmpty()) View.VISIBLE else View.GONE
            binding.rvFavorites.visibility = if (movies.isEmpty()) View.GONE else View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
