package com.example.budgettracker.data.repository

import com.example.budgettracker.data.data.ApiService
import com.example.budgettracker.data.data.DashboardResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DashboardRepository @Inject constructor(private val api: ApiService) {
    fun getDashboardData(): Flow<Result<DashboardResponse>> = flow {
        try {
            val response = api.getDashboardData()
            if (response.isSuccessful && response.body() != null) {
                emit(Result.success(response.body()!!))
            } else {
                emit(Result.failure(Exception("Failed to load dashboard")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    fun getUserInfo(): Flow<Result<com.example.budgettracker.data.data.ProfileResponse>> = flow {
        try {
            val response = api.getUserInfo()
            if (response.isSuccessful && response.body() != null) {
                emit(Result.success(response.body()!!))
            } else {
                emit(Result.failure(Exception("Failed to fetch user info")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}