package com.example.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.myapplication.data.model.GameResult
import com.example.myapplication.data.model.Stats
import com.example.myapplication.data.repository.StatsRepository
import kotlinx.coroutines.flow.StateFlow

class StatsViewModel(private val repository: StatsRepository) : ViewModel() {

    val stats: StateFlow<Stats> = repository.stats

    /**
     * 记录一局游戏结果
     */
    fun recordGameResult(result: GameResult, difficulty: Int = 0) {
        repository.recordGameResult(result, difficulty)
    }

    /**
     * 重置所有统计数据
     */
    fun resetStats() {
        repository.resetStats()
    }
}
