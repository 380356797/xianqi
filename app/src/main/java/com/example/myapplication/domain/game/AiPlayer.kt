package com.example.myapplication.domain.game

import com.example.myapplication.data.model.ChessPiece
import com.example.myapplication.data.model.Move
import com.example.myapplication.data.model.PieceColor
import com.example.myapplication.data.model.PieceType
import com.example.myapplication.data.model.Position
import kotlin.random.Random

class AiPlayer(
    private val difficulty: Int, // 1:简单, 2:中等, 3:困难
    private val color: PieceColor,
    private val chessEngine: ChessEngine = ChessEngine()
) {
    // 棋子基础价值
    private val pieceBaseValues = mapOf(
        PieceType.KING to 10000,
        PieceType.CHARIOT to 500,
        PieceType.CANNON to 250,
        PieceType.HORSE to 250,
        PieceType.ELEPHANT to 120,
        PieceType.ADVISOR to 120,
        PieceType.PAWN to 50
    )

    // 兵/卒位置价值表（红方视角，y越大越靠近敌方）
    private val redPawnPositionValue = arrayOf(
        intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0),   // y=0 敌方底线
        intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0),
        intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0),
        intArrayOf(5, 10, 15, 20, 25, 20, 15, 10, 5),  // y=3 未过河
        intArrayOf(10, 20, 30, 40, 50, 40, 30, 20, 10), // y=4 河边
        intArrayOf(20, 30, 40, 50, 60, 50, 40, 30, 20), // y=5 刚过河
        intArrayOf(30, 40, 50, 60, 70, 60, 50, 40, 30), // y=6
        intArrayOf(40, 50, 60, 70, 80, 70, 60, 50, 40), // y=7
        intArrayOf(50, 60, 70, 80, 90, 80, 70, 60, 50), // y=8
        intArrayOf(60, 70, 80, 90, 100, 90, 80, 70, 60) // y=9 己方底线
    )

    // 车的位置价值表
    private val chariotPositionValue = arrayOf(
        intArrayOf(20, 20, 20, 20, 20, 20, 20, 20, 20),
        intArrayOf(25, 25, 25, 25, 25, 25, 25, 25, 25),
        intArrayOf(15, 15, 20, 20, 20, 20, 20, 15, 15),
        intArrayOf(15, 15, 20, 25, 25, 25, 20, 15, 15),
        intArrayOf(15, 15, 20, 25, 30, 25, 20, 15, 15),
        intArrayOf(15, 15, 20, 25, 30, 25, 20, 15, 15),
        intArrayOf(15, 15, 20, 25, 25, 25, 20, 15, 15),
        intArrayOf(15, 15, 20, 20, 20, 20, 20, 15, 15),
        intArrayOf(25, 25, 25, 25, 25, 25, 25, 25, 25),
        intArrayOf(20, 20, 20, 20, 20, 20, 20, 20, 20)
    )

    // 马的位置价值表
    private val horsePositionValue = arrayOf(
        intArrayOf(10, 15, 10, 10, 10, 10, 10, 15, 10),
        intArrayOf(15, 20, 25, 25, 25, 25, 25, 20, 15),
        intArrayOf(10, 25, 20, 25, 25, 25, 20, 25, 10),
        intArrayOf(10, 25, 25, 30, 30, 30, 25, 25, 10),
        intArrayOf(10, 25, 25, 30, 35, 30, 25, 25, 10),
        intArrayOf(10, 25, 25, 30, 35, 30, 25, 25, 10),
        intArrayOf(10, 25, 20, 25, 25, 25, 20, 25, 10),
        intArrayOf(10, 25, 20, 25, 25, 25, 20, 25, 10),
        intArrayOf(15, 20, 25, 20, 20, 20, 25, 20, 15),
        intArrayOf(10, 15, 10, 10, 10, 10, 10, 15, 10)
    )

    // 炮的位置价值表
    private val cannonPositionValue = arrayOf(
        intArrayOf(10, 10, 10, 15, 10, 15, 10, 10, 10),
        intArrayOf(15, 20, 20, 20, 25, 20, 20, 20, 15),
        intArrayOf(10, 20, 15, 20, 20, 20, 15, 20, 10),
        intArrayOf(10, 20, 20, 25, 25, 25, 20, 20, 10),
        intArrayOf(15, 25, 25, 25, 30, 25, 25, 25, 15),
        intArrayOf(15, 25, 25, 25, 30, 25, 25, 25, 15),
        intArrayOf(10, 20, 20, 25, 25, 25, 20, 20, 10),
        intArrayOf(10, 20, 15, 20, 20, 20, 15, 20, 10),
        intArrayOf(15, 20, 20, 20, 25, 20, 20, 20, 15),
        intArrayOf(10, 10, 10, 15, 10, 15, 10, 10, 10)
    )

    fun getBestMove(board: List<List<ChessPiece?>>): Move? {
        val validMoves = chessEngine.getValidMoves(board, color)
        if (validMoves.isEmpty()) return null

        return when (difficulty) {
            1 -> getEasyMove(validMoves, board)
            2 -> getMediumMove(validMoves, board)
            3 -> getHardMove(validMoves, board)
            else -> getEasyMove(validMoves, board)
        }
    }

    // 简单难度：70%概率优先走能吃子的棋，否则随机走，偶尔会犯傻
    private fun getEasyMove(validMoves: List<Move>, board: List<List<ChessPiece?>>): Move {
        val captureMoves = validMoves.filter { it.capturedPiece != null }

        // 70%概率优先吃子，30%概率随机走任何合法步
        return if (captureMoves.isNotEmpty() && Random.nextFloat() < 0.7f) {
            // 吃子时优先吃价值高的子
            captureMoves.maxByOrNull {
                pieceBaseValues[it.capturedPiece?.type] ?: 0
            } ?: captureMoves.random()
        } else {
            validMoves.random()
        }
    }

    // 中等难度：Alpha-Beta搜索深度2层，基础估值
    private fun getMediumMove(validMoves: List<Move>, board: List<List<ChessPiece?>>): Move {
        return alphaBetaSearch(board, depth = 2, alpha = Int.MIN_VALUE, beta = Int.MAX_VALUE, isMaximizing = true).second ?: validMoves.random()
    }

    // 困难难度：Alpha-Beta搜索深度4层，优化估值，考虑位置价值
    private fun getHardMove(validMoves: List<Move>, board: List<List<ChessPiece?>>): Move {
        return alphaBetaSearch(board, depth = 4, alpha = Int.MIN_VALUE, beta = Int.MAX_VALUE, isMaximizing = true).second ?: validMoves.random()
    }

    // Alpha-Beta剪枝搜索算法
    private fun alphaBetaSearch(
        board: List<List<ChessPiece?>>,
        depth: Int,
        alpha: Int,
        beta: Int,
        isMaximizing: Boolean
    ): Pair<Int, Move?> {
        if (depth == 0) {
            return evaluateBoard(board) to null
        }

        val currentColor = if (isMaximizing) color else if (color == PieceColor.RED) PieceColor.BLACK else PieceColor.RED
        val validMoves = chessEngine.getValidMoves(board, currentColor).sortedByDescending {
            // 按吃子价值排序，提高剪枝效率
            if (it.capturedPiece != null) pieceBaseValues[it.capturedPiece.type] ?: 0 else 0
        }

        if (validMoves.isEmpty()) {
            return if (chessEngine.isInCheck(board, currentColor)) {
                // 被将死，极大值玩家输返回极小值，极小值玩家输返回极大值
                if (isMaximizing) -10000 to null else 10000 to null
            } else {
                // 和棋
                0 to null
            }
        }

        var currentAlpha = alpha
        var currentBeta = beta
        var bestMove: Move? = null

        if (isMaximizing) {
            var maxEval = Int.MIN_VALUE
            for (move in validMoves) {
                val newBoard = chessEngine.makeMove(board, move)
                val eval = alphaBetaSearch(newBoard, depth - 1, currentAlpha, currentBeta, false).first

                if (eval > maxEval) {
                    maxEval = eval
                    bestMove = move
                }

                currentAlpha = maxOf(currentAlpha, eval)
                if (currentBeta <= currentAlpha) {
                    break // Beta剪枝
                }
            }
            return maxEval to bestMove
        } else {
            var minEval = Int.MAX_VALUE
            for (move in validMoves) {
                val newBoard = chessEngine.makeMove(board, move)
                val eval = alphaBetaSearch(newBoard, depth - 1, currentAlpha, currentBeta, true).first

                if (eval < minEval) {
                    minEval = eval
                    bestMove = move
                }

                currentBeta = minOf(currentBeta, eval)
                if (currentBeta <= currentAlpha) {
                    break // Alpha剪枝
                }
            }
            return minEval to bestMove
        }
    }

    // 评估棋盘分数，正数表示AI方占优，负数表示对方占优
    private fun evaluateBoard(board: List<List<ChessPiece?>>): Int {
        var score = 0
        val isAiRed = color == PieceColor.RED

        for (y in 0..9) {
            for (x in 0..8) {
                val piece = board[y][x] ?: continue
                val pieceValue = pieceBaseValues[piece.type] ?: 0
                val positionValue = getPositionValue(piece, x, y)

                val totalValue = pieceValue + positionValue

                if (piece.color == color) {
                    score += totalValue
                } else {
                    score -= totalValue
                }
            }
        }

        // 附加：将军状态加分
        val opponentColor = if (color == PieceColor.RED) PieceColor.BLACK else PieceColor.RED
        if (chessEngine.isInCheck(board, opponentColor)) {
            score += 50 // 将军加50分
        }
        if (chessEngine.isInCheck(board, color)) {
            score -= 50 // 被将军减50分
        }

        return score
    }

    // 获取棋子的位置价值
    private fun getPositionValue(piece: ChessPiece, x: Int, y: Int): Int {
        return when (piece.type) {
            PieceType.PAWN -> {
                if (piece.color == PieceColor.RED) {
                    redPawnPositionValue[y][x]
                } else {
                    redPawnPositionValue[9 - y][x] // 黑方位置表反转
                }
            }
            PieceType.CHARIOT -> {
                if (piece.color == PieceColor.RED) {
                    chariotPositionValue[y][x]
                } else {
                    chariotPositionValue[9 - y][x]
                }
            }
            PieceType.HORSE -> {
                if (piece.color == PieceColor.RED) {
                    horsePositionValue[y][x]
                } else {
                    horsePositionValue[9 - y][x]
                }
            }
            PieceType.CANNON -> {
                if (piece.color == PieceColor.RED) {
                    cannonPositionValue[y][x]
                } else {
                    cannonPositionValue[9 - y][x]
                }
            }
            // 将、士、象的位置相对固定，不额外计算位置价值
            else -> 0
        }
    }
}
