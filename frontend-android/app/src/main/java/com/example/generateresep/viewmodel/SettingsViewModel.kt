package com.example.generateresep.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.generateresep.utils.ThemeManager

class SettingsViewModel : ViewModel() {
    var isNotificationEnabled by mutableStateOf(true)
    
    val isDarkTheme: Boolean
        get() = ThemeManager.isDarkTheme

    fun toggleNotification(enabled: Boolean) {
        isNotificationEnabled = enabled
    }

    fun toggleTheme() {
        ThemeManager.toggleTheme()
    }
}
