package com.example.mvvmbase

sealed class NavigationRequest {
    object GoBack : NavigationRequest()
}
