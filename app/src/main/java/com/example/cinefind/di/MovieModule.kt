package com.example.cinefind.di

import com.example.cinefind.data.repository.MovieRepository
import com.example.cinefind.data.repository.MovieRepositoryImpl
import com.example.cinefind.data.service.MovieApiService
import com.example.cinefind.domain.usecase.GetMovieUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MovieModule {

    @Singleton
    @Provides
    fun provideMovieRepository(apiService: MovieApiService): MovieRepository =
        MovieRepositoryImpl(apiService)

    @Singleton
    @Provides
    fun provideGetMovieUseCase(repository: MovieRepository): GetMovieUseCase =
        GetMovieUseCase(repository)
}
