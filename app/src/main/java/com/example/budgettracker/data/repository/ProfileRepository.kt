package com.example.budgettracker.data.repository

import android.content.Context
import android.net.Uri
import com.example.budgettracker.data.data.ApiService
import com.example.budgettracker.data.data.ChangePasswordRequest
import com.example.budgettracker.data.data.ChangePasswordResponse
import com.example.budgettracker.data.data.ProfileResponse
import com.example.budgettracker.data.data.UpdateProfileRequest
import com.example.budgettracker.util.TokenManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import android.util.Base64
import com.example.budgettracker.data.data.ImageUploadRequest
import org.json.JSONObject
import java.io.InputStream
import javax.inject.Inject

class ProfileRepository @Inject constructor(
    private val api: ApiService,
    @ApplicationContext private val context: Context,
    private val tokenManager: TokenManager
) {

    private fun resolveImageUrl(url: String?): String? {
        if (url.isNullOrBlank()) return null
        
        // If it's already a full URL
        if (url.startsWith("http")) {
            // Force HTTPS for better security and compatibility
            return url.replace("http://", "https://")
        }
        
        // If it's a relative path, prepend BASE_URL
        return "${com.example.budgettracker.util.Constants.BASE_URL}$url"
    }

    fun getUserInfo(): Flow<Result<ProfileResponse>> = flow {
        try {
            val response = api.getUserInfo()

            if (response.isSuccessful && response.body() != null) {
                val profileResponse = response.body()!!
                
                // Resolve image URL
                val resolvedResponse = profileResponse.copy(
                    profileImageUrl = resolveImageUrl(profileResponse.profileImageUrl)
                )

                // Update local storage
                tokenManager.saveUser(
                    name = resolvedResponse.fullName,
                    email = resolvedResponse.email,
                    imageUrl = resolvedResponse.profileImageUrl
                )
                
                emit(Result.success(resolvedResponse))
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = try {
                    val jsonObject = JSONObject(errorBody ?: "")
                    jsonObject.getString("message")
                } catch (e: Exception) {
                    "Failed to fetch profile"
                }
                emit(Result.failure(Exception(errorMessage)))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    fun updateProfile(
        fullName: String,
        phoneNumber: String?,
        address: String?,
        profileImageUrl: String?
    ): Flow<Result<ProfileResponse>> = flow {
        try {
            val request = UpdateProfileRequest(fullName, phoneNumber, address, profileImageUrl)
            val response = api.updateProfile(request)

            if (response.isSuccessful && response.body() != null) {
                val profileResponse = response.body()!!
                
                // Resolve image URL
                val resolvedResponse = profileResponse.copy(
                    profileImageUrl = resolveImageUrl(profileResponse.profileImageUrl)
                )
                
                // Update local storage
                tokenManager.saveUser(
                    name = resolvedResponse.fullName,
                    email = resolvedResponse.email,
                    imageUrl = resolvedResponse.profileImageUrl
                )
                
                emit(Result.success(resolvedResponse))
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = try {
                    val jsonObject = JSONObject(errorBody ?: "")
                    jsonObject.getString("message")
                } catch (e: Exception) {
                    "Failed to update profile"
                }
                emit(Result.failure(Exception(errorMessage)))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    fun changePassword(
        oldPassword: String,
        newPassword: String
    ): Flow<Result<ChangePasswordResponse>> = flow {
        try {
            val request = ChangePasswordRequest(oldPassword, newPassword)
            val response = api.changePassword(request)

            if (response.isSuccessful && response.body() != null) {
                emit(Result.success(response.body()!!))
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = try {
                    val jsonObject = JSONObject(errorBody ?: "")
                    jsonObject.getString("message")
                } catch (e: Exception) {
                    "Failed to change password"
                }
                emit(Result.failure(Exception(errorMessage)))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    fun uploadProfileImage(imageUri: Uri): Flow<Result<String>> = flow {
        try {
            val base64Image = createBase64FromUri(imageUri)
            if (base64Image != null) {
                val request = ImageUploadRequest(image = base64Image)
                val uploadResponse = api.uploadImage(request)
                if (uploadResponse.isSuccessful && uploadResponse.body() != null) {
                    val imageUrl = uploadResponse.body()!!.imageUrl
                    val resolvedUrl = resolveImageUrl(imageUrl)
                    
                    // Also update local storage with the new image URL immediately
                    val currentName = tokenManager.getUserName() ?: ""
                    val currentEmail = tokenManager.getUserEmail() ?: ""
                    tokenManager.saveUser(currentName, currentEmail, resolvedUrl)

                    emit(Result.success(resolvedUrl ?: ""))
                } else {
                    emit(Result.failure(Exception("Failed to upload image")))
                }
            } else {
                emit(Result.failure(Exception("Failed to process image")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    private fun createBase64FromUri(uri: Uri): String? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val bytes = inputStream?.readBytes()
            inputStream?.close()
            if (bytes == null) return null
            val base64 = Base64.encodeToString(bytes, Base64.NO_WRAP)
            "data:image/jpeg;base64,$base64"
        } catch (e: Exception) {
            null
        }
    }
}
