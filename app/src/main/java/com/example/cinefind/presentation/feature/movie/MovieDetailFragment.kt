package com.example.cinefind.presentation.feature.movie

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import coil.load
import com.example.cinefind.data.model.MovieDetailState
import com.example.cinefind.databinding.FragmentMovieDetailBinding
import kotlinx.coroutines.launch

class MovieDetailFragment : Fragment() {

    private var _binding: FragmentMovieDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MovieViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMovieDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.movieDetailState.collect { state ->
                    renderState(state)
                }
            }
        }
    }

    private fun renderState(state: MovieDetailState) {
        when (state) {
            is MovieDetailState.Idle -> Unit
            is MovieDetailState.Loading -> {
                binding.contentGroup.visibility = View.GONE
            }
            is MovieDetailState.Success -> {
                binding.contentGroup.visibility = View.VISIBLE
                val movie = state.movie
                binding.tvTitle.text = movie.originalTitle
                binding.tvOverview.text = movie.overview
                binding.tvRating.text = "★ ${movie.voteAverage} / 10"
                binding.bannerView.load("https://image.tmdb.org/t/p/w780${movie.posterPath}") {
                    crossfade(true)
                    placeholder(android.R.color.darker_gray)
                }
            }
            is MovieDetailState.Error -> {
                binding.contentGroup.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
