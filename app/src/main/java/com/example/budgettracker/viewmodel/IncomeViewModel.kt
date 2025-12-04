package com.example.budgettracker.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgettracker.data.data.AddIncomeRequest
import com.example.budgettracker.data.repository.IncomeRepository
import com.example.budgettracker.data.data.TransactionDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.budgettracker.ui.components.ToastType
import kotlinx.coroutines.flow.asStateFlow
import com.example.budgettracker.util.NotificationHelper

sealed interface IncomeUiState {
    object Loading : IncomeUiState
    data class Success(val transactions: List<TransactionDto>) : IncomeUiState
    data class Error(val message: String) : IncomeUiState
}

data class ToastState(
    val show: Boolean = false,
    val message: String = "",
    val type: ToastType = ToastType.INFO
)

@HiltViewModel
class IncomeViewModel @Inject constructor(
    private val repository: IncomeRepository,
    private val notificationHelper: NotificationHelper
) : ViewModel() {
    private val _toastState = MutableStateFlow(ToastState())
    val toastState: StateFlow<ToastState> = _toastState.asStateFlow()
    private val _uiState = MutableStateFlow<IncomeUiState>(IncomeUiState.Loading)
    val uiState: StateFlow<IncomeUiState> = _uiState

    private val _addIncomeState = MutableStateFlow<Boolean?>(null) // null=idle, true=success, false=error
    val addIncomeState: StateFlow<Boolean?> = _addIncomeState

    init {
        fetchIncome()
    }

    fun fetchIncome() {
        viewModelScope.launch {
            // Silent Refresh: Only show loading if we don't have data yet
            if (_uiState.value !is IncomeUiState.Success) {
                _uiState.value = IncomeUiState.Loading
            }
            
            repository.getAllIncome().collect { result ->
                if (result.isSuccess) {
                    val list = result.getOrThrow()
                    _uiState.value = IncomeUiState.Success(list)
                } else {
                    _uiState.value = IncomeUiState.Error(result.exceptionOrNull()?.message ?: "Error")
                }
            }
        }
    }

    fun addIncome(source: String, amount: String, date: String, iconUrl: String) {
        viewModelScope.launch {
            repository.addIncome(AddIncomeRequest(source, amount, date, iconUrl))
                .collect { result ->
                    if (result.isSuccess) {
                        _addIncomeState.value = true
                        // Trigger Success Toast
                        _toastState.value = ToastState(true, "Income Added Successfully!", ToastType.SUCCESS)
                        fetchIncome() // Refresh list automatically
                    } else {
                        _addIncomeState.value = false
                        // Trigger Error Toast
                        _toastState.value = ToastState(true, "Failed to Add Income", ToastType.ERROR)
                    }
                }
        }
    }

    fun downloadReport() {
        viewModelScope.launch {
            repository.downloadIncomeReport().collect { result ->
                if (result.isSuccess) {
                    val filePath = result.getOrThrow()
                    // Trigger Success Toast
                    _toastState.value = ToastState(true, "Downloaded Successfully!", ToastType.SUCCESS)
                    // Show Notification
                    notificationHelper.showDownloadNotification("Income Report", filePath)
                } else {
                    // Trigger Error Toast
                    _toastState.value = ToastState(true, "Download Failed", ToastType.ERROR)
                }
            }
        }
    }

    fun deleteIncome(id: String) {
        viewModelScope.launch {
            repository.deleteIncome(id).collect { result ->
                if (result.isSuccess) {
                    _toastState.value = ToastState(true, "Income Deleted Successfully!", ToastType.SUCCESS)
                    fetchIncome()
                } else {
                    _toastState.value = ToastState(true, "Failed to Delete Income", ToastType.ERROR)
                }
            }
        }
    }

    fun hideToast() {
        _toastState.value = _toastState.value.copy(show = false)
    }

    fun resetAddIncomeState() {
        _addIncomeState.value = null
    }
}