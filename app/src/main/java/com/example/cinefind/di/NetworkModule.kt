package com.example.cinefind.di

import com.example.cinefind.data.service.MovieApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // TODO: move to local.properties + BuildConfig before production
    private const val TMDB_BASE_URL = "https://api.themoviedb.org/3/"
    private const val TMDB_BEARER_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJlOTcwYjNmZGVhYWYyYjM0MThhZDYzMjIzMzQ3NGQ0NiIsIm5iZiI6MTc4MjcyNTAzMi40Niwic3ViIjoiNmE0MjM5YThkNzAzMTg0NTk3ZDI5YWJhIiwic2NvcGVzIjpbImFwaV9yZWFkIl0sInZlcnNpb24iOjF9.pYCKDZiCl5ab_JBoPs97-SvAuKvEvU-qqVCs1pgS5v0"

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        val authInterceptor = Interceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $TMDB_BEARER_TOKEN")
                .build()
            chain.proceed(request)
        }
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(logging)
            .build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(TMDB_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Singleton
    @Provides
    fun provideMovieApiService(retrofit: Retrofit): MovieApiService =
        retrofit.create(MovieApiService::class.java)
}
