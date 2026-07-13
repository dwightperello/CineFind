package com.example.mvvmbase

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

abstract class BaseActivity<VB : ViewBinding, VM : BaseViewModel>(
    private val bindingFactory: (LayoutInflater) -> VB
) : AppCompatActivity() {

    abstract val viewModel: VM

    private var _binding: VB? = null
    protected val binding: VB get() = _binding!!

    open fun bindView(binding: VB) = Unit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = bindingFactory(layoutInflater)
        setContentView(binding.root)
        bindView(binding)
        initObservers()
    }

    @CallSuper
    protected open fun initObservers() {
        viewModel.showProgress.observe(this) { show ->
            onProgressChanged(show)
        }
        viewModel.networkError.observe(this) {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
        }
        viewModel.genericError.observe(this) { msg ->
            Toast.makeText(this, msg ?: "Something went wrong", Toast.LENGTH_SHORT).show()
        }
        viewModel.navigationRequest.observeForever { request ->
            navigateTo(request)
        }
    }

    open fun onProgressChanged(show: Boolean) {}

    private fun navigateTo(request: NavigationRequest) {
        when (request) {
            is NavigationRequest.GoBack -> finish()
        }
    }

    override fun onDestroy() {
        _binding = null
        viewModel.navigationRequest.removeObservers(this)
        super.onDestroy()
    }
}
