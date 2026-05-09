package com.example.myapplication.data.model

enum class PieceType {
    KING,    // 将/帅
    ADVISOR, // 士/仕
    ELEPHANT,// 象/相
    HORSE,   // 马/馬
    CHARIOT, // 车/車
    CANNON,  // 炮/砲
    PAWN     // 卒/兵
}

enum class PieceColor {
    RED, BLACK
}

data class ChessPiece(
    val type: PieceType,
    val color: PieceColor,
    val position: Position, // (x,y) x:0-8, y:0-9
    val isAlive: Boolean = true
)

data class Position(
    val x: Int,
    val y: Int
) {
    fun isValid(): Boolean = x in 0..8 && y in 0..9
}
