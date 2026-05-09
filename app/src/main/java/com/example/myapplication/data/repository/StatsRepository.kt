package com.example.myapplication.data.repository

import com.example.myapplication.data.model.GameResult
import com.example.myapplication.data.model.Stats
import com.example.myapplication.data.preferences.AppPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 统计数据仓储，管理战绩统计的读取和保存
 */
class StatsRepository(private val appPreferences: AppPreferences) {

    private val _stats = MutableStateFlow(appPreferences.loadStats())
    val stats: StateFlow<Stats> = _stats.asStateFlow()

    fun recordGameResult(result: GameResult, difficulty: Int = 0) {
        val current = _stats.value
        val isWin = result == GameResult.RED_WIN
        val isDraw = result == GameResult.DRAW

        val newStats = current.copy(
            totalGames = current.totalGames + 1,
            wins = current.wins + if (isWin) 1 else 0,
            losses = current.losses + if (!isWin && !isDraw) 1 else 0,
            draws = current.draws + if (isDraw) 1 else 0,
            easyWins = current.easyWins + if (isWin && difficulty == 1) 1 else 0,
            easyTotal = current.easyTotal + if (difficulty == 1) 1 else 0,
            mediumWins = current.mediumWins + if (isWin && difficulty == 2) 1 else 0,
            mediumTotal = current.mediumTotal + if (difficulty == 2) 1 else 0,
            hardWins = current.hardWins + if (isWin && difficulty == 3) 1 else 0,
            hardTotal = current.hardTotal + if (difficulty == 3) 1 else 0
        )

        _stats.value = newStats
        appPreferences.saveStats(newStats)
    }

    fun resetStats() {
        val emptyStats = Stats()
        _stats.value = emptyStats
        appPreferences.clearStats()
    }

    fun reload() {
        _stats.value = appPreferences.loadStats()
    }
}
