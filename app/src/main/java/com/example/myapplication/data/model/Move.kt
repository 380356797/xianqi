package com.example.myapplication.data.model

data class Move(
    val from: Position,
    val to: Position,
    val movedPiece: ChessPiece,
    val capturedPiece: ChessPiece? = null,
    val isCheck: Boolean = false,
    val isCheckmate: Boolean = false
)
