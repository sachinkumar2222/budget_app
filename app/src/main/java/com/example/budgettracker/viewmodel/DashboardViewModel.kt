package com.example.budgettracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgettracker.data.data.DashboardResponse
import com.example.budgettracker.data.data.ProfileResponse
import com.example.budgettracker.data.repository.DashboardRepository
import com.example.budgettracker.util.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.joinAll
import javax.inject.Inject

sealed interface DashboardUiState {
    object Loading : DashboardUiState
    data class Success(val data: DashboardResponse) : DashboardUiState
    data class Error(val message: String) : DashboardUiState
}

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: DashboardRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    // User Info State
    private val _userState = MutableStateFlow<ProfileResponse?>(null)
    val userState: StateFlow<ProfileResponse?> = _userState.asStateFlow()

    // Refreshing State
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    init {
        fetchDashboardData()
        fetchUserInfo()
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            val dashboardJob = launch { fetchDashboardDataInternal() }
            val userJob = launch { fetchUserInfoInternal() }
            joinAll(dashboardJob, userJob)
            _isRefreshing.value = false
        }
    }

    fun fetchDashboardData() {
        viewModelScope.launch {
            fetchDashboardDataInternal()
        }
    }

    private suspend fun fetchDashboardDataInternal() {
        // Only show full screen loading if not refreshing AND we don't have data yet
        if (!_isRefreshing.value && _uiState.value !is DashboardUiState.Success) {
            _uiState.value = DashboardUiState.Loading
        }
        repository.getDashboardData().collect { result ->
            if (result.isSuccess) {
                _uiState.value = DashboardUiState.Success(result.getOrThrow())
            } else {
                _uiState.value = DashboardUiState.Error(result.exceptionOrNull()?.message ?: "Error loading data")
            }
        }
    }

    private fun fetchUserInfo() {
        viewModelScope.launch {
            fetchUserInfoInternal()
        }
    }

    private suspend fun fetchUserInfoInternal() {
        repository.getUserInfo().collect { result ->
            if (result.isSuccess) {
                _userState.value = result.getOrThrow()
            } else {
                // Fallback or ignore
            }
        }
    }
}