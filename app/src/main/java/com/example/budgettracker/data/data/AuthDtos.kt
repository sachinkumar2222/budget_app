package com.example.budgettracker.data.data

import com.google.gson.annotations.SerializedName

// --- Login ---
data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val token: String,
    // The JSON doesn't always have a top-level message, but it has 'user'
    val user: UserDto
)

data class UserDto(
    @SerializedName("_id")
    val id: String,
    val fullName: String, // Matches JSON "fullName"
    val email: String,
    val profileImageUrl: String? // Matches JSON "profileImageUrl"
)

// --- Sign Up ---
data class RegisterRequest(
    val fullName: String,
    val email: String,
    val password: String,
    val profileImage: String
)

data class RegisterResponse(
    val message: String,
    val success: Boolean
)

// --- Image Upload ---
data class ImageUploadRequest(
    val image: String
)

data class ImageUploadResponse(
    @SerializedName("imageUrl")
    val imageUrl: String
)