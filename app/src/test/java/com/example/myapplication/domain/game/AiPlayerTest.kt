package com.example.myapplication.domain.game

import com.example.myapplication.data.model.ChessPiece
import com.example.myapplication.data.model.PieceColor
import com.example.myapplication.data.model.PieceType
import com.example.myapplication.data.model.Position
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

class AiPlayerTest {

    private lateinit var chessEngine: ChessEngine
    private lateinit var easyAi: AiPlayer
    private lateinit var mediumAi: AiPlayer
    private lateinit var hardAi: AiPlayer

    @Before
    fun setup() {
        chessEngine = ChessEngine()
        easyAi = AiPlayer(difficulty = 1, color = PieceColor.BLACK, chessEngine = chessEngine)
        mediumAi = AiPlayer(difficulty = 2, color = PieceColor.BLACK, chessEngine = chessEngine)
        hardAi = AiPlayer(difficulty = 3, color = PieceColor.BLACK, chessEngine = chessEngine)
    }

    @Test
    fun `easy AI should return valid move`() {
        val board = chessEngine.initializeBoard()
        val move = easyAi.getBestMove(board)
        assertNotNull(move)
        assertTrue(chessEngine.isMoveValid(board, move!!))
    }

    @Test
    fun `medium AI should return valid move`() {
        val board = chessEngine.initializeBoard()
        val move = mediumAi.getBestMove(board)
        assertNotNull(move)
        assertTrue(chessEngine.isMoveValid(board, move!!))
    }

    @Test
    fun `hard AI should return valid move`() {
        val board = chessEngine.initializeBoard()
        val move = hardAi.getBestMove(board)
        assertNotNull(move)
        assertTrue(chessEngine.isMoveValid(board, move!!))
    }

    @Test
    fun `AI should return null when no valid moves available`() {
        // 创建一个空棋盘，没有棋子，AI没有合法走法
        val emptyBoard = List(10) { MutableList<ChessPiece?>(9) { null } }
        val move = easyAi.getBestMove(emptyBoard)
        assertNull(move)
    }
}
