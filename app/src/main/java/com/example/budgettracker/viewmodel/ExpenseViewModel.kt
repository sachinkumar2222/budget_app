package com.example.budgettracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgettracker.data.data.AddTransactionRequest
import com.example.budgettracker.data.data.TransactionDto
import com.example.budgettracker.data.repository.ExpenseRepository
import com.example.budgettracker.ui.components.ToastType // Import your ToastType enum
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.budgettracker.util.NotificationHelper

// Sealed Interface for Main UI State
sealed interface ExpenseUiState {
    object Loading : ExpenseUiState
    data class Success(val transactions: List<TransactionDto>) : ExpenseUiState
    data class Error(val message: String) : ExpenseUiState
}

@HiltViewModel
class ExpenseViewModel @Inject constructor(
    private val repository: ExpenseRepository,
    private val notificationHelper: NotificationHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow<ExpenseUiState>(ExpenseUiState.Loading)
    val uiState: StateFlow<ExpenseUiState> = _uiState.asStateFlow()

    private val _addExpenseState = MutableStateFlow<Boolean?>(null)
    val addExpenseState: StateFlow<Boolean?> = _addExpenseState.asStateFlow()

    // ▼▼▼ 1. NEW TOAST STATE ▼▼▼
    private val _toastState = MutableStateFlow(ToastState())
    val toastState: StateFlow<ToastState> = _toastState.asStateFlow()

    init {
        fetchExpenses()
    }

    fun fetchExpenses() {
        viewModelScope.launch {
            // Silent Refresh: Only show loading if we don't have data yet
            if (_uiState.value !is ExpenseUiState.Success) {
                _uiState.value = ExpenseUiState.Loading
            }
            
            repository.getAllExpenses().collect { result ->
                if (result.isSuccess) {
                    _uiState.value = ExpenseUiState.Success(result.getOrThrow())
                } else {
                    _uiState.value = ExpenseUiState.Error(result.exceptionOrNull()?.message ?: "Error")
                }
            }
        }
    }

    fun addExpense(category: String, amount: String, date: String, iconUrl: String) {
        viewModelScope.launch {
            repository.addExpense(AddTransactionRequest(category, amount, date, iconUrl))
                .collect { result ->
                    if (result.isSuccess) {
                        _addExpenseState.value = true
                        // Trigger Success Toast
                        _toastState.value = ToastState(true, "Expense Added Successfully!", ToastType.SUCCESS)
                        fetchExpenses()
                    } else {
                        _addExpenseState.value = false
                        // Trigger Error Toast
                        _toastState.value = ToastState(true, "Failed to Add Expense", ToastType.ERROR)
                    }
                }
        }
    }

    fun downloadReport() {
        viewModelScope.launch {
            repository.downloadExpenseReport().collect { result ->
                if (result.isSuccess) {
                    val filePath = result.getOrThrow()
                    // Trigger Success Toast
                    _toastState.value = ToastState(true, "Downloaded Successfully!", ToastType.SUCCESS)
                    // Show Notification
                    notificationHelper.showDownloadNotification("Expense Report", filePath)
                } else {
                    // Trigger Error Toast
                    _toastState.value = ToastState(true, "Download Failed", ToastType.ERROR)
                }
            }
        }
    }

    fun deleteExpense(id: String) {
        viewModelScope.launch {
            repository.deleteExpense(id).collect { result ->
                if (result.isSuccess) {
                    _toastState.value = ToastState(true, "Expense Deleted Successfully!", ToastType.SUCCESS)
                    fetchExpenses()
                } else {
                    _toastState.value = ToastState(true, "Failed to Delete Expense", ToastType.ERROR)
                }
            }
        }
    }

    fun hideToast() {
        _toastState.value = _toastState.value.copy(show = false)
    }

    fun resetAddExpenseState() {
        _addExpenseState.value = null
    }
}