package com.example.budgettracker.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgettracker.data.data.LoginRequest
import com.example.budgettracker.data.repository.AuthRepository
import com.example.budgettracker.util.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.budgettracker.ui.components.ToastType

sealed interface AuthUiState {
    object Idle : AuthUiState
    object Loading : AuthUiState
    object LoginSuccess : AuthUiState
    object SignUpSuccess : AuthUiState
    object Authenticated : AuthUiState
    data class Error(val message: String) : AuthUiState
}

data class AuthToastState(
    val show: Boolean = false,
    val message: String = "",
    val type: ToastType = ToastType.INFO
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    val logoutSignal = tokenManager.logoutSignal
    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState

    private val _toastState = MutableStateFlow(AuthToastState())
    val toastState: StateFlow<AuthToastState> = _toastState.asStateFlow()

    // Expose User Info
    val userName: String
        get() = tokenManager.getUserName() ?: "User"
    val userEmail: String
        get() = tokenManager.getUserEmail() ?: "user@example.com"

    fun checkAuthStatus() {
        viewModelScope.launch {
            val token = tokenManager.getToken()
            val isRemembered = tokenManager.getRememberMe()

            delay(1000)

            if (!token.isNullOrEmpty() && isRemembered) {
                _uiState.value = AuthUiState.Authenticated
            } else {
                if (!isRemembered) {
                    tokenManager.clear()
                }
                _uiState.value = AuthUiState.Idle
            }
        }
    }

    fun logout() {
        tokenManager.clear()
        _uiState.value = AuthUiState.Idle
        _toastState.value = AuthToastState(true, "Logged out successfully!", ToastType.SUCCESS)
    }

    fun hideToast() {
        _toastState.value = _toastState.value.copy(show = false)
    }

    fun login(email: String, password: String, rememberMe: Boolean) {
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            repository.login(LoginRequest(email, password), rememberMe).collect { result ->
                if (result.isSuccess) {
                    _uiState.value = AuthUiState.LoginSuccess
                } else {
                    _uiState.value =
                        AuthUiState.Error(result.exceptionOrNull()?.message ?: "Login Failed")
                }
            }
        }
    }

    fun signUp(fullName: String, email: String, password: String, imageUri: Uri?) {
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            repository.signUp(fullName, email, password, imageUri).collect { result ->
                if (result.isSuccess) {
                    _uiState.value = AuthUiState.SignUpSuccess
                } else {
                    _uiState.value =
                        AuthUiState.Error(result.exceptionOrNull()?.message ?: "Signup Failed")
                }
            }
        }
    }

    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }
}