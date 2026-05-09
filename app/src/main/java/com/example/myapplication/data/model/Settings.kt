package com.example.myapplication.data.model

data class Settings(
    val musicVolume: Float = 0.7f,
    val soundEffectVolume: Float = 0.8f,
    val isMusicEnabled: Boolean = true,
    val isSoundEffectEnabled: Boolean = true,
    val isTimerEnabled: Boolean = true,
    val maxUndoCount: Int = 3,
    val defaultAiDifficulty: Int = 1
)
