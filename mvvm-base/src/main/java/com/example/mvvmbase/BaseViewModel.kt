package com.example.mvvmbase

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

abstract class BaseViewModel : ViewModel() {

    protected val _showProgress = SingleLiveEvent<Boolean>()
    val showProgress: LiveData<Boolean> = _showProgress

    protected val _networkError = SingleLiveEvent<Unit>()
    val networkError: LiveData<Unit> = _networkError

    protected val _genericError = SingleLiveEvent<String?>()
    val genericError: LiveData<String?> = _genericError

    protected val _navigationRequest = SingleLiveEvent<NavigationRequest>()
    val navigationRequest: LiveData<NavigationRequest> = _navigationRequest

    protected fun showProgress() { _showProgress.value = true }
    protected fun hideProgress() { _showProgress.value = false }

    fun requestNavigation(request: NavigationRequest) {
        _navigationRequest.value = request
    }

    protected fun <R> CoroutineScope.launchSafely(
        execute: suspend CoroutineScope.() -> R,
        onSuccess: (R) -> Unit,
        onError: (Throwable) -> Unit = { _genericError.value = it.message }
    ): Job = launch {
        try {
            showProgress()
            val result = execute(this)
            onSuccess(result)
        } catch (e: CancellationException) {
            // ignored
        } catch (e: Exception) {
            onError(e)
        } finally {
            hideProgress()
        }
    }
}
