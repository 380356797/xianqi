package com.example.myapplication.data.repository

import com.example.myapplication.data.model.Settings
import com.example.myapplication.data.preferences.AppPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 设置数据仓储，管理设置的读取和保存
 */
class SettingsRepository(private val appPreferences: AppPreferences) {

    private val _settings = MutableStateFlow(appPreferences.loadSettings())
    val settings: StateFlow<Settings> = _settings.asStateFlow()

    fun updateSettings(update: (Settings) -> Settings) {
        val newSettings = update(_settings.value)
        _settings.value = newSettings
        appPreferences.saveSettings(newSettings)
    }

    fun reload() {
        _settings.value = appPreferences.loadSettings()
    }
}
