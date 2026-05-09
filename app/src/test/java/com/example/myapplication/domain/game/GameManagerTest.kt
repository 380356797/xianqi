package com.example.myapplication.domain.game

import com.example.myapplication.data.model.GameStatus
import com.example.myapplication.data.model.PieceColor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

class GameManagerTest {

    private lateinit var gameManager: GameManager
    private lateinit var chessEngine: ChessEngine

    @Before
    fun setup() {
        chessEngine = ChessEngine()
        gameManager = GameManager(chessEngine, CoroutineScope(Dispatchers.Unconfined))
    }

    @Test
    fun `startNewGame should initialize game state correctly`() = runBlocking {
        gameManager.startNewGame(isAiGame = true, aiDifficulty = 2, maxUndo = 5, timerEnabled = true)
        val state = gameManager.gameState.first()

        assertEquals(GameStatus.PLAYING, state.status)
        assertEquals(PieceColor.RED, state.currentTurn)
        assertEquals(32, state.board.sumOf { row -> row.count { it != null } }) // 32个棋子
        assertTrue(state.isAiGame)
        assertEquals(2, state.aiDifficulty)
        assertEquals(15 * 60 * 1000, state.redTimeLeft)
        assertEquals(15 * 60 * 1000, state.blackTimeLeft)
        assertEquals(5, gameManager.getRemainingUndoCount())
    }

    @Test
    fun `makeMove should update game state correctly for valid move`() = runBlocking {
        gameManager.startNewGame()
        val initialState = gameManager.gameState.first()
        val chariot = initialState.board[9][0]!! // 红方左下角车
        val validMove = chessEngine.getValidMovesForPiece(initialState.board, chariot).first()

        val result = gameManager.makeMove(validMove)
        assertTrue(result)

        val newState = gameManager.gameState.first()
        assertEquals(PieceColor.BLACK, newState.currentTurn) // 回合切换到黑方
        assertNull(newState.board[9][0]) // 原位置为空
        assertNotNull(newState.board[validMove.to.y][validMove.to.x]) // 新位置有棋子
        assertEquals(1, newState.moveHistory.size) // 走法历史加1
    }

    @Test
    fun `makeMove should return false for invalid move`() = runBlocking {
        gameManager.startNewGame()
        val initialState = gameManager.gameState.first()
        val chariot = initialState.board[9][0]!!

        // 非法走法：直接走到黑方底线
        val invalidMove = chessEngine.getValidMovesForPiece(initialState.board, chariot).first().copy(
            to = com.example.myapplication.data.model.Position(0, 0)
        )

        val result = gameManager.makeMove(invalidMove)
        assertFalse(result)

        val newState = gameManager.gameState.first()
        assertEquals(PieceColor.RED, newState.currentTurn) // 回合未切换
        assertEquals(0, newState.moveHistory.size) // 走法历史未增加
    }

    @Test
    fun `undo should revert last move correctly`() = runBlocking {
        gameManager.startNewGame(maxUndo = 3)
        val initialState = gameManager.gameState.first()
        val chariot = initialState.board[9][0]!!
        val validMove = chessEngine.getValidMovesForPiece(initialState.board, chariot).first()

        gameManager.makeMove(validMove)
        val afterMoveState = gameManager.gameState.first()
        assertEquals(1, afterMoveState.moveHistory.size)
        assertEquals(PieceColor.BLACK, afterMoveState.currentTurn)

        val undoResult = gameManager.undo()
        assertTrue(undoResult)

        val afterUndoState = gameManager.gameState.first()
        assertEquals(0, afterUndoState.moveHistory.size)
        assertEquals(PieceColor.RED, afterUndoState.currentTurn) // 回合回到红方
        assertNotNull(afterUndoState.board[9][0]) // 车回到原位置
        assertEquals(2, gameManager.getRemainingUndoCount()) // 剩余悔棋次数减1
    }

    @Test
    fun `surrender should end game immediately`() = runBlocking {
        gameManager.startNewGame()
        val result = gameManager.surrender()
        assertTrue(result)

        val state = gameManager.gameState.first()
        assertEquals(GameStatus.FINISHED, state.status)
        assertEquals(com.example.myapplication.data.model.GameResult.BLACK_WIN, state.result) // 红方认输，黑方胜
    }

    @Test
    fun `pause and resume should work correctly`() = runBlocking {
        gameManager.startNewGame(timerEnabled = true)
        val initialState = gameManager.gameState.first()
        val initialRedTime = initialState.redTimeLeft

        gameManager.pause()
        assertEquals(GameStatus.PAUSED, gameManager.gameState.first().status)

        // 等待1秒，时间应该不会减少
        kotlinx.coroutines.delay(1100)
        val afterPauseState = gameManager.gameState.first()
        assertEquals(initialRedTime, afterPauseState.redTimeLeft)

        gameManager.resume()
        assertEquals(GameStatus.PLAYING, gameManager.gameState.first().status)

        // 等待1秒，时间应该减少1秒
        kotlinx.coroutines.delay(1100)
        val afterResumeState = gameManager.gameState.first()
        assertEquals(initialRedTime - 1000, afterResumeState.redTimeLeft)
    }

    @Test
    fun `getRemainingUndoCount should return correct value`() = runBlocking {
        gameManager.startNewGame(maxUndo = 3)
        assertEquals(3, gameManager.getRemainingUndoCount())

        val initialState = gameManager.gameState.first()
        val chariot = initialState.board[9][0]!!
        val validMove = chessEngine.getValidMovesForPiece(initialState.board, chariot).first()

        gameManager.makeMove(validMove)
        gameManager.undo()
        assertEquals(2, gameManager.getRemainingUndoCount())

        gameManager.makeMove(validMove)
        gameManager.undo()
        assertEquals(1, gameManager.getRemainingUndoCount())

        gameManager.makeMove(validMove)
        gameManager.undo()
        assertEquals(0, gameManager.getRemainingUndoCount())

        // 悔棋次数用完，不能再悔棋
        gameManager.makeMove(validMove)
        val undoResult = gameManager.undo()
        assertFalse(undoResult)
        assertEquals(0, gameManager.getRemainingUndoCount())
    }
}
