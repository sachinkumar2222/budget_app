package com.example.budgettracker.di

import android.content.Context
import com.example.budgettracker.data.AuthInterceptor
import com.example.budgettracker.data.TokenExpirationInterceptor
import com.example.budgettracker.data.data.ApiService
import com.example.budgettracker.data.repository.AuthRepository
import com.example.budgettracker.util.Constants
import com.example.budgettracker.util.TokenManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder().create()

    @Provides
    @Singleton
    fun provideTokenManager(@ApplicationContext context: Context): TokenManager {
        return TokenManager(context)
    }

    @Provides
    @Singleton
    fun provideAuthInterceptor(tokenManager: TokenManager): AuthInterceptor {
        return AuthInterceptor(tokenManager)
    }

    // ▼▼▼ ADDED: Token Expiration Interceptor Provider ▼▼▼
    @Provides
    @Singleton
    fun provideTokenExpirationInterceptor(tokenManager: TokenManager): TokenExpirationInterceptor {
        return TokenExpirationInterceptor(tokenManager)
    }

    // ▼▼▼ COMBINED & CORRECTED: Only ONE provideOkHttpClient function ▼▼▼
    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        tokenExpirationInterceptor: TokenExpirationInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor) // 1. Add Auth Token
            .addInterceptor(tokenExpirationInterceptor) // 2. Check for Expiration (401)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(gson: Gson, okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        api: ApiService,
        @ApplicationContext context: Context,
        tokenManager: TokenManager
    ): AuthRepository {
        return AuthRepository(api, context, tokenManager)
    }
}