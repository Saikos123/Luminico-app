package com.example.luminico

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class SessionManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("luminico_user_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_PROFILE_IMAGE = "profile_image_uri"
    }

    fun saveUserSession(email: String, rememberMe: Boolean) {
        if (rememberMe) {
            prefs.edit {
                putBoolean(KEY_IS_LOGGED_IN, true)
                putString(KEY_USER_EMAIL, email)
            }
        } else {
            clearSession()
        }
    }

    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_IS_LOGGED_IN, false)

    fun getSavedEmail(): String = prefs.getString(KEY_USER_EMAIL, "") ?: ""

    fun saveProfileImageUri(uriString: String) {
        prefs.edit { putString(KEY_PROFILE_IMAGE, uriString) }
    }

    fun getProfileImageUri(): String {
        return prefs.getString(KEY_PROFILE_IMAGE, "") ?: ""
    }

    fun clearSession() {
        prefs.edit { clear() }
    }
}
