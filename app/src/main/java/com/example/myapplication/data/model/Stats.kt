package com.example.myapplication.data.model

data class Stats(
    val totalGames: Int = 0,
    val wins: Int = 0,
    val losses: Int = 0,
    val draws: Int = 0,
    val easyWins: Int = 0,
    val easyTotal: Int = 0,
    val mediumWins: Int = 0,
    val mediumTotal: Int = 0,
    val hardWins: Int = 0,
    val hardTotal: Int = 0
) {
    val winRate: Float get() = if (totalGames == 0) 0f else wins.toFloat() / totalGames
    val easyWinRate: Float get() = if (easyTotal == 0) 0f else easyWins.toFloat() / easyTotal
    val mediumWinRate: Float get() = if (mediumTotal == 0) 0f else mediumWins.toFloat() / mediumTotal
    val hardWinRate: Float get() = if (hardTotal == 0) 0f else hardWins.toFloat() / hardTotal
}
