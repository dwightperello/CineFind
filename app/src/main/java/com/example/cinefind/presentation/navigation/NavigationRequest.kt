package com.example.cinefind.presentation.navigation

sealed class NavigationRequest {
    object GoBack : NavigationRequest()
}
