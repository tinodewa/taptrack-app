package com.hit.taptrack.utils.helpers

import com.russhwolf.settings.Settings

class UserSettings(private val settings: Settings) {

    companion object {
        // --- KEYS ---
        private const val KEY_JWT_TOKEN = "jwt_token"
        private const val KEY_USER_ROLE = "user_role"
        private const val KEY_PROFILE_ID = "profile_id"
        private const val KEY_LAST_SYNC = "last_sync_timestamp"
    }

    // ==========================================
    // AUTHENTICATION & CREDENTIALS
    // ==========================================

    /**
     * Menyimpan data login sekaligus agar rapi saat dipanggil dari Repository
     */
    fun saveAuthData(token: String, role: String?, profileId: String?) {
        settings.putString(KEY_JWT_TOKEN, token)
        role?.let { settings.putString(KEY_USER_ROLE, it) }
        profileId?.let { settings.putString(KEY_PROFILE_ID, it) }
    }

    fun getToken(): String? {
        return settings.getStringOrNull(KEY_JWT_TOKEN)
    }

    fun getRole(): String {
        // Ambil peran, kembalikan 'USER' sebagai default (penonton/guest)
        return settings.getString(KEY_USER_ROLE, "USER")
    }

    fun getProfileId(): String? {
        return settings.getStringOrNull(KEY_PROFILE_ID)
    }

    /**
     * Mengecek apakah token ada dan tidak kosong. Sangat berguna untuk Splash Screen!
     */
    fun isLoggedIn(): Boolean {
        val token = getToken()
        return !token.isNullOrBlank()
    }

    /**
     * Digunakan saat user Logout di Profile Screen.
     */
    fun clearAuthData() {
        settings.remove(KEY_JWT_TOKEN)
        settings.remove(KEY_USER_ROLE)
        settings.remove(KEY_PROFILE_ID)
    }


    // ==========================================
    // OFFLINE SYNC (UNTUK FITUR COMMITTEE)
    // ==========================================

    fun saveLastTimeSync(time: String) {
        settings.putString(KEY_LAST_SYNC, time)
    }

    fun getLastTimeSync(): String? {
        return settings.getStringOrNull(KEY_LAST_SYNC)
    }
}