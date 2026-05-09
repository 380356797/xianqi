package com.example.myapplication.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.data.preferences.AppPreferences
import com.example.myapplication.data.repository.SettingsRepository
import com.example.myapplication.data.repository.StatsRepository

/**
 * ViewModel工厂，负责创建各个ViewModel实例
 */
class AppViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    private val appPreferences = AppPreferences(context)
    private val settingsRepository = SettingsRepository(appPreferences)
    private val statsRepository = StatsRepository(appPreferences)

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> {
                SettingsViewModel(settingsRepository) as T
            }
            modelClass.isAssignableFrom(StatsViewModel::class.java) -> {
                StatsViewModel(statsRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
