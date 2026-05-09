package com.example.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.myapplication.data.model.Settings
import com.example.myapplication.data.repository.SettingsRepository
import kotlinx.coroutines.flow.StateFlow

class SettingsViewModel(private val repository: SettingsRepository) : ViewModel() {

    val settings: StateFlow<Settings> = repository.settings

    fun updateMusicVolume(volume: Float) {
        repository.updateSettings { it.copy(musicVolume = volume) }
    }

    fun updateSoundEffectVolume(volume: Float) {
        repository.updateSettings { it.copy(soundEffectVolume = volume) }
    }

    fun toggleMusicEnabled() {
        repository.updateSettings { it.copy(isMusicEnabled = !it.isMusicEnabled) }
    }

    fun toggleSoundEffectEnabled() {
        repository.updateSettings { it.copy(isSoundEffectEnabled = !it.isSoundEffectEnabled) }
    }

    fun toggleTimerEnabled() {
        repository.updateSettings { it.copy(isTimerEnabled = !it.isTimerEnabled) }
    }

    fun updateMaxUndoCount(count: Int) {
        repository.updateSettings { it.copy(maxUndoCount = count) }
    }

    fun updateDefaultAiDifficulty(difficulty: Int) {
        repository.updateSettings { it.copy(defaultAiDifficulty = difficulty) }
    }
}
