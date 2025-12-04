package com.example.budgettracker.data.data

import com.example.budgettracker.util.Constants
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Streaming

interface ApiService {

    @POST(Constants.API_PATHS.AUTH.LOGIN)
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @POST(Constants.API_PATHS.AUTH.REGISTER)
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<RegisterResponse>

    @POST(Constants.API_PATHS.AUTH.UPLOAD_IMAGE)
    suspend fun uploadImage(
        @Body request: ImageUploadRequest
    ): Response<ImageUploadResponse>

    @GET(Constants.API_PATHS.DASHBOARD.GET_DATA)
    suspend fun getDashboardData(): Response<DashboardResponse>

    @GET("/api/v1/income/get")
    suspend fun getIncome(): Response<List<TransactionDto>>

    @POST("/api/v1/income/add")
    suspend fun addIncome(@Body request: AddIncomeRequest): Response<AddIncomeResponse>

    @GET("/api/v1/expense/get")
    suspend fun getExpense(): Response<ExpenseResponse>

    @POST("/api/v1/expense/add")
    suspend fun addExpense(@Body request: AddTransactionRequest): Response<AddTransactionResponse>

    @Streaming
    @GET("/api/v1/income/downloadexcel")
    suspend fun downloadIncomeReport(): Response<ResponseBody>

    @Streaming
    @GET("/api/v1/expense/downloadexcel")
    suspend fun downloadExpenseReport(): Response<ResponseBody>

    // Profile Management Endpoints
    @GET("/api/v1/auth/getUser")
    suspend fun getUserInfo(): Response<ProfileResponse>

    @PUT("/api/v1/profile/update")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): Response<ProfileResponse>

    @PUT("/api/v1/profile/change-password")
    suspend fun changePassword(@Body request: ChangePasswordRequest): Response<ChangePasswordResponse>

    @DELETE("/api/v1/income/{id}")
    suspend fun deleteIncome(@Path("id") id: String): Response<DeleteResponse>

    @DELETE("/api/v1/expense/{id}")
    suspend fun deleteExpense(@Path("id") id: String): Response<DeleteResponse>
}