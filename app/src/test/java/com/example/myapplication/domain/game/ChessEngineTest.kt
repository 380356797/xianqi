package com.example.myapplication.domain.game

import com.example.myapplication.data.model.PieceColor
import com.example.myapplication.data.model.PieceType
import com.example.myapplication.data.model.Position
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

class ChessEngineTest {

    private lateinit var chessEngine: ChessEngine

    @Before
    fun setup() {
        chessEngine = ChessEngine()
    }

    @Test
    fun `initializeBoard should place all pieces correctly`() {
        val board = chessEngine.initializeBoard()

        // 验证黑方将的位置
        val blackKing = board[0][4]
        assertNotNull(blackKing)
        assertEquals(PieceType.KING, blackKing?.type)
        assertEquals(PieceColor.BLACK, blackKing?.color)

        // 验证红方帅的位置
        val redKing = board[9][4]
        assertNotNull(redKing)
        assertEquals(PieceType.KING, redKing?.type)
        assertEquals(PieceColor.RED, redKing?.color)

        // 验证棋子总数
        var pieceCount = 0
        board.forEach { row ->
            row.forEach { piece ->
                if (piece != null) pieceCount++
            }
        }
        assertEquals(32, pieceCount)
    }

    @Test
    fun `getValidMovesForPiece should return correct moves for chariot at initial position`() {
        val board = chessEngine.initializeBoard()
        val chariot = board[9][0]!! // 红方左下角车

        val moves = chessEngine.getValidMovesForPiece(board, chariot)
        // 初始位置的车前面y=8和y=7是空的，y=6是己方兵阻挡，所以可以前进2步
        assertEquals(2, moves.size)
        assertTrue(moves.any { it.to == Position(0, 8) })
        assertTrue(moves.any { it.to == Position(0, 7) })
    }

    @Test
    fun `getValidMovesForPiece should return correct moves for horse at initial position`() {
        val board = chessEngine.initializeBoard()
        val horse = board[9][1]!! // 红方左下角马

        val moves = chessEngine.getValidMovesForPiece(board, horse)
        // 初始位置的马有2个合法走法
        assertEquals(2, moves.size)
        assertTrue(moves.any { it.to == Position(0, 7) })
        assertTrue(moves.any { it.to == Position(2, 7) })
    }

    @Test
    fun `isMoveValid should correctly validate moves`() {
        val board = chessEngine.initializeBoard()
        val chariot = board[9][0]!!

        // 合法走法：前进一步
        val validMove = chessEngine.getValidMovesForPiece(board, chariot)[0]
        assertTrue(chessEngine.isMoveValid(board, validMove))

        // 非法走法：直接走到底
        val invalidMove = validMove.copy(to = Position(0, 0))
        assertFalse(chessEngine.isMoveValid(board, invalidMove))
    }

    @Test
    fun `isInCheck should return false for initial board state`() {
        val board = chessEngine.initializeBoard()
        // 初始状态没有将军
        assertFalse(chessEngine.isInCheck(board, PieceColor.RED))
        assertFalse(chessEngine.isInCheck(board, PieceColor.BLACK))
    }

    @Test
    fun `makeMove should correctly update board state`() {
        val board = chessEngine.initializeBoard()
        val chariot = board[9][0]!!
        val move = chessEngine.getValidMovesForPiece(board, chariot)[0]

        val newBoard = chessEngine.makeMove(board, move)

        // 原位置应该为空
        assertNull(newBoard[9][0])
        // 新位置应该有车
        val movedChariot = newBoard[move.to.y][move.to.x]
        assertNotNull(movedChariot)
        assertEquals(PieceType.CHARIOT, movedChariot?.type)
        assertEquals(PieceColor.RED, movedChariot?.color)
    }
}
