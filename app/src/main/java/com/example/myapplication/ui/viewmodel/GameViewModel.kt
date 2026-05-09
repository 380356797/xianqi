package com.example.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.model.GameState
import com.example.myapplication.data.model.GameStatus
import com.example.myapplication.data.model.Move
import com.example.myapplication.data.model.PieceColor
import com.example.myapplication.domain.game.ChessEngine
import com.example.myapplication.domain.game.GameManager
import kotlinx.coroutines.flow.StateFlow

class GameViewModel : ViewModel() {
    private val chessEngine = ChessEngine()
    private val gameManager = GameManager(chessEngine, coroutineScope = viewModelScope)

    val gameState: StateFlow<GameState> = gameManager.gameState

    /**
     * 开始双人对战游戏
     */
    fun startTwoPlayerGame(timerEnabled: Boolean = true, maxUndo: Int = 3) {
        gameManager.startNewGame(
            isAiGame = false,
            maxUndo = maxUndo,
            timerEnabled = timerEnabled
        )
    }

    /**
     * 开始人机对战游戏
     */
    fun startAiGame(aiDifficulty: Int = 1, timerEnabled: Boolean = true, maxUndo: Int = 3) {
        gameManager.startNewGame(
            isAiGame = true,
            aiDifficulty = aiDifficulty,
            maxUndo = maxUndo,
            timerEnabled = timerEnabled
        )
    }

    /**
     * 获取当前可走的所有合法走法
     */
    fun getValidMoves(): List<Move> {
        val state = gameState.value
        if (state.status != GameStatus.PLAYING) return emptyList()
        return chessEngine.getValidMoves(state.board, state.currentTurn)
    }

    /**
     * 执行走棋
     */
    fun makeMove(move: Move) {
        gameManager.makeMove(move)
    }

    /**
     * 悔棋
     */
    fun undo() {
        gameManager.undo()
    }

    /**
     * 认输
     */
    fun surrender() {
        gameManager.surrender()
    }

    /**
     * 暂停游戏
     */
    fun pause() {
        gameManager.pause()
    }

    /**
     * 继续游戏
     */
    fun resume() {
        gameManager.resume()
    }

    /**
     * 获取剩余悔棋次数
     */
    fun getRemainingUndoCount(): Int {
        return gameManager.getRemainingUndoCount()
    }

    override fun onCleared() {
        super.onCleared()
        gameManager.destroy()
    }
}
