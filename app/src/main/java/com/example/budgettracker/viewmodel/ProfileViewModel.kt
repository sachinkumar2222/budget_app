package com.example.budgettracker.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgettracker.data.data.ProfileResponse
import com.example.budgettracker.data.repository.ProfileRepository
import com.example.budgettracker.ui.components.ToastType
import com.example.budgettracker.util.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ProfileUiState {
    object Idle : ProfileUiState
    object Loading : ProfileUiState
    data class Success(val message: String) : ProfileUiState
    data class Error(val message: String) : ProfileUiState
}

data class ProfileData(
    val fullName: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val address: String = "",
    val profileImageUrl: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: ProfileRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Idle)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _profileData = MutableStateFlow(ProfileData())
    val profileData: StateFlow<ProfileData> = _profileData.asStateFlow()

    private val _uploadingImage = MutableStateFlow(false)
    val uploadingImage: StateFlow<Boolean> = _uploadingImage.asStateFlow()

    private val _notificationEnabled = MutableStateFlow(true)
    val notificationEnabled: StateFlow<Boolean> = _notificationEnabled.asStateFlow()

    private val _toastState = MutableStateFlow(ToastState())
    val toastState: StateFlow<ToastState> = _toastState.asStateFlow()

    init {
        loadProfileData()
        _notificationEnabled.value = tokenManager.getNotificationPreference()
    }

    fun toggleNotification(enabled: Boolean) {
        _notificationEnabled.value = enabled
        tokenManager.saveNotificationPreference(enabled)
    }

    fun refreshProfile() {
        loadProfileData()
    }

    private fun loadProfileData() {
        viewModelScope.launch {
            repository.getUserInfo().collect { result ->
                if (result.isSuccess) {
                    val profile = result.getOrNull()
                    if (profile != null) {
                        _profileData.value = ProfileData(
                            fullName = profile.fullName,
                            email = profile.email,
                            phoneNumber = profile.phoneNumber ?: "",
                            address = profile.address ?: "",
                            profileImageUrl = profile.profileImageUrl
                        )
                    }
                } else {
                    // Fallback to TokenManager if API fails
                    _profileData.value = ProfileData(
                        fullName = tokenManager.getUserName() ?: "",
                        email = tokenManager.getUserEmail() ?: "",
                        phoneNumber = "",
                        address = "",
                        profileImageUrl = tokenManager.getUserImage()
                    )
                }
            }
        }
    }

    fun updateProfile(fullName: String, phoneNumber: String, address: String) {
        _uiState.value = ProfileUiState.Loading
        viewModelScope.launch {
            repository.updateProfile(
                fullName = fullName,
                phoneNumber = phoneNumber.ifBlank { null },
                address = address.ifBlank { null },
                profileImageUrl = _profileData.value.profileImageUrl
            ).collect { result ->
                if (result.isSuccess) {
                    val profile = result.getOrNull()
                    if (profile != null) {
                        _profileData.value = ProfileData(
                            fullName = profile.fullName,
                            email = profile.email,
                            phoneNumber = profile.phoneNumber ?: "",
                            address = profile.address ?: "",
                            profileImageUrl = profile.profileImageUrl
                        )
                    }
                    _uiState.value = ProfileUiState.Success("Profile updated successfully")
                    _toastState.value = ToastState(true, "Profile updated successfully", ToastType.SUCCESS)
                } else {
                    val errorMsg = result.exceptionOrNull()?.message ?: "Failed to update profile"
                    _uiState.value = ProfileUiState.Error(errorMsg)
                    _toastState.value = ToastState(true, errorMsg, ToastType.ERROR)
                }
            }
        }
    }

    fun uploadImage(imageUri: Uri) {
        _uploadingImage.value = true
        viewModelScope.launch {
            repository.uploadProfileImage(imageUri).collect { result ->
                _uploadingImage.value = false
                if (result.isSuccess) {
                    val imageUrl = result.getOrNull()
                    _profileData.value = _profileData.value.copy(profileImageUrl = imageUrl)
                    _toastState.value = ToastState(true, "Image uploaded successfully", ToastType.SUCCESS)
                } else {
                    _uiState.value = ProfileUiState.Error("Failed to upload image")
                    _toastState.value = ToastState(true, "Failed to upload image", ToastType.ERROR)
                }
            }
        }
    }

    fun changePassword(oldPassword: String, newPassword: String) {
        _uiState.value = ProfileUiState.Loading
        viewModelScope.launch {
            repository.changePassword(oldPassword, newPassword).collect { result ->
                if (result.isSuccess) {
                    _uiState.value = ProfileUiState.Success("Password changed successfully")
                    _toastState.value = ToastState(true, "Password changed successfully", ToastType.SUCCESS)
                } else {
                    val errorMsg = result.exceptionOrNull()?.message ?: "Failed to change password"
                    _uiState.value = ProfileUiState.Error(errorMsg)
                    _toastState.value = ToastState(true, errorMsg, ToastType.ERROR)
                }
            }
        }
    }

    fun hideToast() {
        _toastState.value = _toastState.value.copy(show = false)
    }

    fun resetState() {
        _uiState.value = ProfileUiState.Idle
    }
}
