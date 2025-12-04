package com.example.budgettracker.util

object Constants {

    const val BASE_URL = "https://budget-tracker-app-pnwq.onrender.com"

//    const val BASE_URL = "http://10.0.2.2:8000"
    object API_PATHS {
        object AUTH {
            const val LOGIN = "/api/v1/auth/login"
            const val REGISTER = "/api/v1/auth/register"
            const val GET_USER_INFO = "/api/v1/auth/getUser"
            const val UPLOAD_IMAGE = "/api/v1/auth/upload-image"
        }

        // Ensure this exists
        object DASHBOARD {
            const val GET_DATA = "/api/v1/dashboard"
        }
    }
}