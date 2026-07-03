package com.example.generateresep.utils

object UserSession {
    var currentUsername: String = ""

    // Fungsi untuk membersihkan sesi saat logout
    fun clearSession() {
        currentUsername = ""
    }
}