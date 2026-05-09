package com.example.myapplication.data.model

enum class GameStatus {
    PREPARING, PLAYING, PAUSED, FINISHED
}

enum class GameResult {
    RED_WIN, BLACK_WIN, DRAW, ONGOING
}

data class GameState(
    val status: GameStatus = GameStatus.PREPARING,
    val currentTurn: PieceColor = PieceColor.RED,
    val board: List<List<ChessPiece?>> = emptyBoard(),
    val moveHistory: List<Move> = emptyList(),
    val result: GameResult = GameResult.ONGOING,
    val redTimeLeft: Long = 0,
    val blackTimeLeft: Long = 0,
    val isAiGame: Boolean = false,
    val aiDifficulty: Int = 1 // 1:简单, 2:中等, 3:困难
) {
    companion object {
        fun emptyBoard(): List<List<ChessPiece?>> {
            return List(10) { MutableList<ChessPiece?>(9) { null } }
        }
    }
}
