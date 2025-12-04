package com.example.budgettracker.data.repository

import android.content.Context
import android.net.Uri
import android.util.Base64
import com.example.budgettracker.data.data.ApiService
import com.example.budgettracker.data.data.ImageUploadRequest
import com.example.budgettracker.data.data.LoginRequest
import com.example.budgettracker.data.data.LoginResponse
import com.example.budgettracker.data.data.RegisterRequest
import com.example.budgettracker.data.data.RegisterResponse
import com.example.budgettracker.util.TokenManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.json.JSONObject
import java.io.InputStream
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val api: ApiService,
    @ApplicationContext private val context: Context,
    private val tokenManager: TokenManager
) {

    fun login(request: LoginRequest, rememberMe: Boolean): Flow<Result<LoginResponse>> = flow {
        try {
            val response = api.login(request)
            if (response.isSuccessful && response.body() != null) {
                val loginResponse = response.body()!!

                // 1. Always save Token & User Data so the current session works immediately
                tokenManager.saveToken(loginResponse.token)
                tokenManager.saveUser(
                    name = loginResponse.user?.fullName ?: "User",
                    email = loginResponse.user?.email ?: request.email,
                    imageUrl = loginResponse.user?.profileImageUrl
                )

                // 2. CRITICAL: Explicitly save the "Remember Me" preference
                tokenManager.setRememberMe(rememberMe)

                emit(Result.success(loginResponse))
            } else {
                val errorBody = response.errorBody()?.string()
                // Try to parse the error message
                val errorMessage = try {
                    val jsonObject = JSONObject(errorBody ?: "")
                    jsonObject.getString("message")
                } catch (e: Exception) {
                    "Login Failed"
                }
                emit(Result.failure(Exception(errorMessage)))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    fun signUp(fullName: String, email: String, password: String, imageUri: Uri?): Flow<Result<RegisterResponse>> = flow {
        try {
            var imageUrl = ""
            if (imageUri != null) {
                val imageUploadRequest = createBase64ImageUploadRequest(imageUri)
                if (imageUploadRequest != null) {
                    val uploadResponse = api.uploadImage(imageUploadRequest)
                    if (uploadResponse.isSuccessful && uploadResponse.body() != null) {
                        imageUrl = uploadResponse.body()!!.imageUrl
                    }
                }
            }
            val request = RegisterRequest(fullName, email, password, imageUrl)
            val response = api.register(request)

            if (response.isSuccessful && response.body() != null) {
                emit(Result.success(response.body()!!))
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = try {
                    val jsonObject = JSONObject(errorBody ?: "")
                    jsonObject.getString("message")
                } catch (e: Exception) {
                    "Unknown error occurred"
                }
                emit(Result.failure(Exception(errorMessage)))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    private fun createBase64ImageUploadRequest(uri: Uri): ImageUploadRequest? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val bytes = inputStream?.readBytes()
            inputStream?.close()
            if (bytes == null) return null
            val base64Image = Base64.encodeToString(bytes, Base64.NO_WRAP)
            ImageUploadRequest("data:image/jpeg;base64,$base64Image")
        } catch (e: Exception) {
            null
        }
    }
}