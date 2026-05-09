package com.example.myapplication.domain.game

import com.example.myapplication.data.model.GameResult
import com.example.myapplication.data.model.GameState
import com.example.myapplication.data.model.GameStatus
import com.example.myapplication.data.model.Move
import com.example.myapplication.data.model.PieceColor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GameManager(
    private val chessEngine: ChessEngine = ChessEngine(),
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) {
    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private var aiPlayer: AiPlayer? = null
    private var timerJob: Job? = null
    private var undoCount = 0
    private var maxUndoCount = 3

    /**
     * 开始新游戏
     * @param isAiGame 是否是AI对战模式
     * @param aiDifficulty AI难度 1-3
     * @param maxUndo 最大悔棋次数
     * @param timerEnabled 是否开启计时器
     */
    fun startNewGame(
        isAiGame: Boolean = false,
        aiDifficulty: Int = 1,
        maxUndo: Int = 3,
        timerEnabled: Boolean = true
    ) {
        val initialBoard = chessEngine.initializeBoard()
        maxUndoCount = maxUndo
        undoCount = 0

        _gameState.value = GameState(
            status = GameStatus.PLAYING,
            currentTurn = PieceColor.RED,
            board = initialBoard,
            moveHistory = emptyList(),
            result = GameResult.ONGOING,
            redTimeLeft = if (timerEnabled) 15 * 60 * 1000 else 0, // 每方15分钟
            blackTimeLeft = if (timerEnabled) 15 * 60 * 1000 else 0,
            isAiGame = isAiGame,
            aiDifficulty = aiDifficulty
        )

        if (isAiGame) {
            aiPlayer = AiPlayer(aiDifficulty, PieceColor.BLACK, chessEngine)
        }

        if (timerEnabled) {
            startTimer()
        }
    }

    /**
     * 执行走棋
     * @param move 走法
     * @return 是否走棋成功
     */
    fun makeMove(move: Move): Boolean {
        val currentState = _gameState.value
        if (currentState.status != GameStatus.PLAYING) return false
        if (move.movedPiece.color != currentState.currentTurn) return false
        if (!chessEngine.isMoveValid(currentState.board, move)) return false

        // 执行走法
        val newBoard = chessEngine.makeMove(currentState.board, move)

        // 检查走法是否会导致自己被将军，这种走法不允许
        val isInCheckAfterMove = chessEngine.isInCheck(newBoard, currentState.currentTurn)
        if (isInCheckAfterMove) return false

        // 检查走法是否将军对方
        val opponentColor = if (currentState.currentTurn == PieceColor.RED) PieceColor.BLACK else PieceColor.RED
        val isOpponentInCheck = chessEngine.isInCheck(newBoard, opponentColor)

        // 更新走法信息，标记是否将军
        val updatedMove = move.copy(
            isCheck = isOpponentInCheck,
            isCheckmate = chessEngine.isCheckmate(newBoard, opponentColor)
        )

        // 更新游戏状态
        val newMoveHistory = currentState.moveHistory + updatedMove
        val nextTurn = opponentColor
        val gameResult = chessEngine.getGameResult(newBoard, currentState.currentTurn)
        val isGameFinished = gameResult != GameResult.ONGOING

        val newState = currentState.copy(
            board = newBoard,
            currentTurn = nextTurn,
            moveHistory = newMoveHistory,
            result = gameResult,
            status = if (isGameFinished) GameStatus.FINISHED else GameStatus.PLAYING
        )

        _gameState.value = newState

        // 如果游戏结束，停止计时器
        if (isGameFinished) {
            stopTimer()
        }

        // 如果是AI回合，自动走棋
        if (!isGameFinished && currentState.isAiGame && nextTurn == PieceColor.BLACK) {
            coroutineScope.launch {
                delay(500) // 模拟AI思考时间，更有真实感
                aiPlayer?.getBestMove(newBoard)?.let { aiMove ->
                    makeMove(aiMove)
                }
            }
        }

        return true
    }

    /**
     * 悔棋
     * @return 是否悔棋成功
     */
    fun undo(): Boolean {
        val currentState = _gameState.value
        if (currentState.status != GameStatus.PLAYING) return false
        if (currentState.moveHistory.isEmpty()) return false
        if (undoCount >= maxUndoCount) return false

        var newMoveHistory = currentState.moveHistory.dropLast(1)
        var previousTurn = if (currentState.currentTurn == PieceColor.RED) PieceColor.BLACK else PieceColor.RED

        // 如果是AI游戏，且上一步是AI走的，需要撤销两步（AI和玩家各一步）
        if (currentState.isAiGame && previousTurn == PieceColor.BLACK && newMoveHistory.isNotEmpty()) {
            newMoveHistory = newMoveHistory.dropLast(1)
            previousTurn = PieceColor.RED
        }

        // 重建棋盘状态
        val newBoard = chessEngine.initializeBoard()
        newMoveHistory.forEach { move ->
            chessEngine.makeMove(newBoard, move)
        }

        _gameState.value = currentState.copy(
            board = newBoard,
            currentTurn = previousTurn,
            moveHistory = newMoveHistory,
            result = GameResult.ONGOING
        )

        undoCount++
        return true
    }

    /**
     * 认输
     * @return 是否成功认输
     */
    fun surrender(): Boolean {
        val currentState = _gameState.value
        if (currentState.status != GameStatus.PLAYING) return false

        // 谁认输谁输
        val result = if (currentState.currentTurn == PieceColor.RED) GameResult.BLACK_WIN else GameResult.RED_WIN
        _gameState.value = currentState.copy(
            status = GameStatus.FINISHED,
            result = result
        )

        stopTimer()
        return true
    }

    /**
     * 暂停游戏
     */
    fun pause() {
        if (_gameState.value.status == GameStatus.PLAYING) {
            _gameState.value = _gameState.value.copy(status = GameStatus.PAUSED)
            stopTimer()
        }
    }

    /**
     * 继续游戏
     */
    fun resume() {
        if (_gameState.value.status == GameStatus.PAUSED) {
            _gameState.value = _gameState.value.copy(status = GameStatus.PLAYING)
            startTimer()
        }
    }

    /**
     * 启动计时器
     */
    private fun startTimer() {
        stopTimer()
        timerJob = coroutineScope.launch {
            while (_gameState.value.status == GameStatus.PLAYING) {
                delay(1000)
                val currentState = _gameState.value
                if (currentState.redTimeLeft <= 0 || currentState.blackTimeLeft <= 0) {
                    // 超时判负
                    val result = if (currentState.redTimeLeft <= 0) GameResult.BLACK_WIN else GameResult.RED_WIN
                    _gameState.value = currentState.copy(
                        status = GameStatus.FINISHED,
                        result = result
                    )
                    stopTimer()
                    break
                }

                if (currentState.currentTurn == PieceColor.RED) {
                    _gameState.value = currentState.copy(redTimeLeft = currentState.redTimeLeft - 1000)
                } else {
                    _gameState.value = currentState.copy(blackTimeLeft = currentState.blackTimeLeft - 1000)
                }
            }
        }
    }

    /**
     * 停止计时器
     */
    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    /**
     * 销毁资源，避免内存泄漏
     */
    fun destroy() {
        stopTimer()
        aiPlayer = null
    }

    /**
     * 获取当前剩余悔棋次数
     */
    fun getRemainingUndoCount(): Int {
        return maxUndoCount - undoCount
    }
}
