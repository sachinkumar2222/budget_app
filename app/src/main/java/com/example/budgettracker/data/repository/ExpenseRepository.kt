package com.example.budgettracker.data.repository

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.example.budgettracker.data.data.AddTransactionRequest
import com.example.budgettracker.data.data.AddTransactionResponse
import com.example.budgettracker.data.data.ApiService
import com.example.budgettracker.data.data.DeleteResponse
import com.example.budgettracker.data.data.TransactionDto
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

class ExpenseRepository @Inject constructor(
    private val api: ApiService,
    @ApplicationContext private val context: Context
) {

    fun getAllExpenses(): Flow<Result<List<TransactionDto>>> = flow {
        try {
            val response = api.getExpense()
            if (response.isSuccessful && response.body() != null) {
                emit(Result.success(response.body()!!.expense))
            } else {
                emit(Result.failure(Exception("Failed to fetch expenses")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    fun addExpense(request: AddTransactionRequest): Flow<Result<AddTransactionResponse>> = flow {
        try {
            val response = api.addExpense(request)
            if (response.isSuccessful && response.body() != null) {
                emit(Result.success(response.body()!!))
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Failed to add expense"
                emit(Result.failure(Exception(errorMsg)))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    fun downloadExpenseReport(): Flow<Result<String>> = flow {
        try {
            val response = api.downloadExpenseReport()
            if (response.isSuccessful && response.body() != null) {
                val fileName = "Expense_Report_${System.currentTimeMillis()}.xlsx"
                val filePath = saveFileToDownloads(response.body()!!, fileName)

                if (filePath != null) {
                    emit(Result.success(filePath))
                } else {
                    emit(Result.failure(Exception("Failed to create file")))
                }
            } else {
                emit(Result.failure(Exception("Download failed")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    private suspend fun saveFileToDownloads(body: ResponseBody, fileName: String): String? {
        return withContext(Dispatchers.IO) {
            var inputStream: InputStream? = null
            var outputStream: OutputStream? = null

            try {
                inputStream = body.byteStream()

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val contentValues = ContentValues().apply {
                        put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                        put(MediaStore.MediaColumns.MIME_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                    }
                    val resolver = context.contentResolver
                    val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

                    if (uri != null) {
                        outputStream = resolver.openOutputStream(uri)
                        return@withContext if (outputStream != null && writeStream(inputStream, outputStream)) uri.toString() else null
                    } else {
                        return@withContext null
                    }
                } else {
                    val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    val file = File(path, fileName)
                    outputStream = FileOutputStream(file)
                    return@withContext if (writeStream(inputStream, outputStream)) file.absolutePath else null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return@withContext null
            } finally {
                inputStream?.close()
                outputStream?.close()
            }
        }
    }

    fun deleteExpense(id: String): Flow<Result<DeleteResponse>> = flow {
        try {
            val response = api.deleteExpense(id)
            if (response.isSuccessful && response.body() != null) {
                emit(Result.success(response.body()!!))
            } else {
                emit(Result.failure(Exception("Failed to delete expense")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    private fun writeStream(input: InputStream, output: OutputStream): Boolean {
        return try {
            val buffer = ByteArray(4096)
            var bytesRead: Int
            while (input.read(buffer).also { bytesRead = it } != -1) {
                output.write(buffer, 0, bytesRead)
            }
            output.flush()
            true
        } catch (e: Exception) {
            false
        }
    }
}