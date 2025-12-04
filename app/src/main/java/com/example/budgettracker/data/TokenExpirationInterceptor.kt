package com.example.budgettracker.data

import com.example.budgettracker.util.TokenManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class TokenExpirationInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())

        // ▼▼▼ CHECK FOR 401 UNAUTHORIZED ▼▼▼
        if (response.code == 401) {
            // Token is invalid or expired
            runBlocking {
                tokenManager.triggerLogout()
            }
        }

        return response
    }
}