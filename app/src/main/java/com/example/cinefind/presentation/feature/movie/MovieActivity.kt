package com.example.cinefind.presentation.feature.movie

import android.view.View
import androidx.activity.viewModels
import com.example.cinefind.R
import com.example.cinefind.databinding.ActivityMovieBinding
import com.example.cinefind.presentation.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MovieActivity : BaseActivity<ActivityMovieBinding, MovieViewModel>(
    ActivityMovieBinding::inflate
) {

    override val viewModel: MovieViewModel by viewModels()

    override fun bindView(binding: ActivityMovieBinding) {
        if (supportFragmentManager.findFragmentById(R.id.fragmentContainer) == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainer, MovieListFragment())
                .commit()
        }
    }

    override fun initObservers() {
        super.initObservers()
    }

    override fun onProgressChanged(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }
}
