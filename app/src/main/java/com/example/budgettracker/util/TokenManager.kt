package com.example.budgettracker.util

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext context: Context
) {
    private var prefs: SharedPreferences =
        context.getSharedPreferences("budget_prefs", Context.MODE_PRIVATE)

    // ▼▼▼ 1. LOGOUT EVENT SIGNAL ▼▼▼
    private val _logoutSignal = MutableSharedFlow<Unit>(replay = 0)
    val logoutSignal = _logoutSignal.asSharedFlow()

    fun saveToken(token: String) {
        prefs.edit().putString("jwt_token", token).apply()
    }

    fun getToken(): String? {
        return prefs.getString("jwt_token", null)
    }

    fun saveUser(name: String, email: String, imageUrl: String?) {
        val editor = prefs.edit()
        editor.putString("user_name", name)
        editor.putString("user_email", email)
        if (imageUrl != null) {
            editor.putString("user_image", imageUrl)
        }
        editor.apply()
    }

    fun getUserName(): String? = prefs.getString("user_name", "User")
    fun getUserEmail(): String? = prefs.getString("user_email", "user@example.com")
    fun getUserImage(): String? = prefs.getString("user_image", null)

    fun setRememberMe(enabled: Boolean) {
        prefs.edit().putBoolean("remember_me", enabled).apply()
    }

    fun getRememberMe(): Boolean {
        return prefs.getBoolean("remember_me", false)
    }

    fun saveNotificationPreference(enabled: Boolean) {
        prefs.edit().putBoolean("notifications_enabled", enabled).apply()
    }

    fun getNotificationPreference(): Boolean {
        return prefs.getBoolean("notifications_enabled", true)
    }

    fun clear() {
        prefs.edit().clear().apply()
    }

    // ▼▼▼ 2. EMIT LOGOUT SIGNAL ▼▼▼
    suspend fun triggerLogout() {
        clear() // Delete data
        _logoutSignal.emit(Unit) // Tell the app to switch screens
    }
}