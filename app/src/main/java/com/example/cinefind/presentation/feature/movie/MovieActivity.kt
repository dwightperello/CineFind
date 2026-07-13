package com.example.cinefind.presentation.feature.movie

import android.view.View
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import com.example.cinefind.R
import com.example.cinefind.databinding.ActivityMovieBinding
import com.example.mvvmbase.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MovieActivity : BaseActivity<ActivityMovieBinding, MovieViewModel>(
    ActivityMovieBinding::inflate
) {

    override val viewModel: MovieViewModel by viewModels()

    override fun bindView(binding: ActivityMovieBinding) {
        if (supportFragmentManager.findFragmentByTag(TAG_DISCOVER) == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainer, MovieListFragment(), TAG_DISCOVER)
                .commit()
        }
        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_discover -> switchTab(TAG_DISCOVER) { MovieListFragment() }
                R.id.nav_favorites -> switchTab(TAG_FAVORITES) { FavoritesFragment() }
            }
            true
        }

        supportFragmentManager.addOnBackStackChangedListener {
            val inDetail = supportFragmentManager.backStackEntryCount > 0
            binding.bottomNavigation.visibility = if (inDetail) View.GONE else View.VISIBLE
        }
    }

    private fun switchTab(tag: String, factory: () -> Fragment) {
        val existing = supportFragmentManager.findFragmentByTag(tag)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, existing ?: factory(), tag)
            .commit()
    }

    override fun initObservers() {
        super.initObservers()
    }

    override fun onProgressChanged(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }

    companion object {
        private const val TAG_DISCOVER = "discover"
        private const val TAG_FAVORITES = "favorites"
    }
}
