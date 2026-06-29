package com.example.cinefind.presentation.feature.movie

import android.view.View
import androidx.activity.viewModels
import coil.load
import com.example.cinefind.databinding.ActivityMovieBinding
import com.example.cinefind.presentation.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MovieActivity : BaseActivity<ActivityMovieBinding, MovieViewModel>(
    ActivityMovieBinding::inflate
) {

    override val viewModel: MovieViewModel by viewModels()

    override fun bindView(binding: ActivityMovieBinding) {
        viewModel.loadMovie(id = 13)
    }

    override fun initObservers() {
        super.initObservers()
        viewModel.movie.observe(this) { state ->
            binding.tvTitle.text = state.movie.title
            binding.tvOverview.text = state.movie.overview
            binding.tvRating.text = "★ ${state.movie.voteAverage} / 10"
            binding.bannerView.load("https://image.tmdb.org/t/p/w780${state.movie.posterPath}") {
                crossfade(true)
                placeholder(android.R.color.darker_gray)
            }
        }
    }

    override fun onProgressChanged(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }
}
