package com.example.budgettracker.data.data

import com.google.gson.annotations.SerializedName

// Profile Update Request
data class UpdateProfileRequest(
    val fullName: String,
    val phoneNumber: String?,
    val address: String?,
    val profileImageUrl: String?
)

// Profile Response
data class ProfileResponse(
    @SerializedName("_id")
    val id: String,
    val fullName: String,
    val email: String,
    val phoneNumber: String?,
    val address: String?,
    val profileImageUrl: String?
)

// Change Password Request
data class ChangePasswordRequest(
    val oldPassword: String,
    val newPassword: String
)

// Change Password Response
data class ChangePasswordResponse(
    val message: String
)
