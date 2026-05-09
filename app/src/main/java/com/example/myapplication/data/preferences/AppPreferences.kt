package com.example.myapplication.data.preferences

import android.content.Context
import android.content.SharedPreferences
import com.example.myapplication.data.model.Settings
import com.example.myapplication.data.model.Stats

/**
 * SharedPreferences封装类，管理应用设置和统计数据
 */
class AppPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("chess_prefs", Context.MODE_PRIVATE)

    // ==================== 设置相关 ====================

    companion object Keys {
        const val MUSIC_VOLUME = "music_volume"
        const val SOUND_EFFECT_VOLUME = "sound_effect_volume"
        const val IS_MUSIC_ENABLED = "is_music_enabled"
        const val IS_SOUND_EFFECT_ENABLED = "is_sound_effect_enabled"
        const val IS_TIMER_ENABLED = "is_timer_enabled"
        const val MAX_UNDO_COUNT = "max_undo_count"
        const val DEFAULT_AI_DIFFICULTY = "default_ai_difficulty"

        const val TOTAL_GAMES = "total_games"
        const val WINS = "wins"
        const val LOSSES = "losses"
        const val DRAWS = "draws"
        const val EASY_WINS = "easy_wins"
        const val EASY_TOTAL = "easy_total"
        const val MEDIUM_WINS = "medium_wins"
        const val MEDIUM_TOTAL = "medium_total"
        const val HARD_WINS = "hard_wins"
        const val HARD_TOTAL = "hard_total"
    }

    fun loadSettings(): Settings {
        return Settings(
            musicVolume = prefs.getFloat(MUSIC_VOLUME, 0.7f),
            soundEffectVolume = prefs.getFloat(SOUND_EFFECT_VOLUME, 0.8f),
            isMusicEnabled = prefs.getBoolean(IS_MUSIC_ENABLED, true),
            isSoundEffectEnabled = prefs.getBoolean(IS_SOUND_EFFECT_ENABLED, true),
            isTimerEnabled = prefs.getBoolean(IS_TIMER_ENABLED, true),
            maxUndoCount = prefs.getInt(MAX_UNDO_COUNT, 3),
            defaultAiDifficulty = prefs.getInt(DEFAULT_AI_DIFFICULTY, 1)
        )
    }

    fun saveSettings(settings: Settings) {
        prefs.edit().apply {
            putFloat(MUSIC_VOLUME, settings.musicVolume)
            putFloat(SOUND_EFFECT_VOLUME, settings.soundEffectVolume)
            putBoolean(IS_MUSIC_ENABLED, settings.isMusicEnabled)
            putBoolean(IS_SOUND_EFFECT_ENABLED, settings.isSoundEffectEnabled)
            putBoolean(IS_TIMER_ENABLED, settings.isTimerEnabled)
            putInt(MAX_UNDO_COUNT, settings.maxUndoCount)
            putInt(DEFAULT_AI_DIFFICULTY, settings.defaultAiDifficulty)
            apply()
        }
    }

    // ==================== 统计相关 ====================

    fun loadStats(): Stats {
        return Stats(
            totalGames = prefs.getInt(TOTAL_GAMES, 0),
            wins = prefs.getInt(WINS, 0),
            losses = prefs.getInt(LOSSES, 0),
            draws = prefs.getInt(DRAWS, 0),
            easyWins = prefs.getInt(EASY_WINS, 0),
            easyTotal = prefs.getInt(EASY_TOTAL, 0),
            mediumWins = prefs.getInt(MEDIUM_WINS, 0),
            mediumTotal = prefs.getInt(MEDIUM_TOTAL, 0),
            hardWins = prefs.getInt(HARD_WINS, 0),
            hardTotal = prefs.getInt(HARD_TOTAL, 0)
        )
    }

    fun saveStats(stats: Stats) {
        prefs.edit().apply {
            putInt(TOTAL_GAMES, stats.totalGames)
            putInt(WINS, stats.wins)
            putInt(LOSSES, stats.losses)
            putInt(DRAWS, stats.draws)
            putInt(EASY_WINS, stats.easyWins)
            putInt(EASY_TOTAL, stats.easyTotal)
            putInt(MEDIUM_WINS, stats.mediumWins)
            putInt(MEDIUM_TOTAL, stats.mediumTotal)
            putInt(HARD_WINS, stats.hardWins)
            putInt(HARD_TOTAL, stats.hardTotal)
            apply()
        }
    }

    fun clearStats() {
        prefs.edit().apply {
            remove(TOTAL_GAMES)
            remove(WINS)
            remove(LOSSES)
            remove(DRAWS)
            remove(EASY_WINS)
            remove(EASY_TOTAL)
            remove(MEDIUM_WINS)
            remove(MEDIUM_TOTAL)
            remove(HARD_WINS)
            remove(HARD_TOTAL)
            apply()
        }
    }
}
