package com.example.cinefind.presentation.feature.movie

import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.example.cinefind.databinding.ActivityMovieBinding
import com.example.cinefind.presentation.adapter.GenreAdapter
import com.example.cinefind.presentation.base.BaseActivity
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MovieActivity : BaseActivity<ActivityMovieBinding, MovieViewModel>(
    ActivityMovieBinding::inflate
) {

    override val viewModel: MovieViewModel by viewModels()

    private lateinit var genreAdapter: GenreAdapter

    override fun bindView(binding: ActivityMovieBinding) {
        setupGenreRecyclerView()
        viewModel.loadMovie(id = 13)
        viewModel.loadUpcomingMovies()
        viewModel.loadGenres()
    }

    private fun setupGenreRecyclerView() {
        genreAdapter = GenreAdapter { genre ->
            // TODO: call viewModel.loadMoviesByGenre(genre.id) when implemented
            Log.d("mapping", "Selected genre: ${genre.name} (id=${genre.id})")
        }
        binding.rvGenres.apply {
            layoutManager = LinearLayoutManager(this@MovieActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = genreAdapter
        }
    }

    override fun initObservers() {
        super.initObservers()
        viewModel.movie.observe(this) { state ->
            binding.tvTitle.text = state.movie.originalTitle
            binding.tvOverview.text = state.movie.overview
            binding.tvRating.text = "★ ${state.movie.voteAverage} / 10"
            binding.bannerView.load("https://image.tmdb.org/t/p/w780${state.movie.posterPath}") {
                crossfade(true)
                placeholder(android.R.color.darker_gray)
            }
        }
        viewModel.movieUpcoming.observe(this) { state ->
            Log.d("mapping", Gson().toJson(state))
        }
        viewModel.genre.observe(this) { state ->
            genreAdapter.submitList(state.genres)
        }
    }

    override fun onProgressChanged(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }
}
