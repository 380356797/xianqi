package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.model.GameResult
import com.example.myapplication.data.model.GameStatus
import com.example.myapplication.data.model.PieceColor
import com.example.myapplication.domain.utils.SoundManager
import com.example.myapplication.ui.components.ChessBoard
import com.example.myapplication.ui.components.QIconButton
import com.example.myapplication.ui.viewmodel.GameViewModel

@Composable
fun GameScreen(
    viewModel: GameViewModel,
    onBackToMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val gameState by viewModel.gameState.collectAsState()
    var showSurrenderDialog by remember { mutableStateOf(false) }
    var showGameOverDialog by remember { mutableStateOf(false) }
    val soundManager = remember { SoundManager(context) }

    // 初始化音效管理器
    LaunchedEffect(Unit) {
        soundManager.init()
    }

    // 释放音效资源
    DisposableEffect(Unit) {
        onDispose {
            soundManager.release()
        }
    }

    // 监听走法变化，播放音效
    val moveHistorySize = gameState.moveHistory.size
    var lastMoveHistorySize by remember { mutableStateOf(0) }

    LaunchedEffect(moveHistorySize) {
        if (moveHistorySize > lastMoveHistorySize && lastMoveHistorySize > 0) {
            val lastMove = gameState.moveHistory.lastOrNull()
            if (lastMove != null) {
                if (lastMove.capturedPiece != null) {
                    soundManager.playCaptureSound()
                } else {
                    soundManager.playMoveSound()
                }
                if (lastMove.isCheck) {
                    soundManager.playCheckSound()
                }
            }
        }
        lastMoveHistorySize = moveHistorySize
    }

    // 游戏结束时显示弹窗和播放音效
    LaunchedEffect(gameState.status) {
        if (gameState.status == GameStatus.FINISHED) {
            showGameOverDialog = true
            if (gameState.result == GameResult.RED_WIN) {
                soundManager.playWinSound()
            } else if (gameState.result == GameResult.BLACK_WIN) {
                soundManager.playLoseSound()
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 顶部操作栏
            TopActionBar(
                onBackClick = onBackToMenu,
                onUndoClick = { viewModel.undo() },
                onSurrenderClick = { showSurrenderDialog = true },
                onPauseClick = {
                    if (gameState.status == GameStatus.PLAYING) {
                        viewModel.pause()
                    } else if (gameState.status == GameStatus.PAUSED) {
                        viewModel.resume()
                    }
                },
                isPaused = gameState.status == GameStatus.PAUSED,
                undoCount = viewModel.getRemainingUndoCount(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 黑方信息栏
            PlayerInfoBar(
                playerName = if (gameState.isAiGame) "AI" else "黑方",
                timeLeft = formatTime(gameState.blackTimeLeft),
                isCurrentTurn = gameState.currentTurn == PieceColor.BLACK,
                isBlack = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(4.dp))

            // 棋盘区域
            ChessBoard(
                board = gameState.board,
                validMoves = viewModel.getValidMoves(),
                onMoveMade = { viewModel.makeMove(it) },
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            // 红方信息栏
            PlayerInfoBar(
                playerName = "红方",
                timeLeft = formatTime(gameState.redTimeLeft),
                isCurrentTurn = gameState.currentTurn == PieceColor.RED,
                isBlack = false,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // 认输确认弹窗
        if (showSurrenderDialog) {
            AlertDialog(
                onDismissRequest = { showSurrenderDialog = false },
                icon = { Icon(Icons.Default.Warning, contentDescription = null) },
                title = { Text("确认认输") },
                text = { Text("确定要认输吗？本局将判负。") },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.surrender()
                            showSurrenderDialog = false
                        }
                    ) {
                        Text("认输")
                    }
                },
                dismissButton = {
                    Button(onClick = { showSurrenderDialog = false }) {
                        Text("取消")
                    }
                }
            )
        }

        // 游戏结束弹窗
        if (showGameOverDialog) {
            GameOverDialog(
                result = gameState.result,
                onNewGame = {
                    if (gameState.isAiGame) {
                        viewModel.startAiGame(aiDifficulty = gameState.aiDifficulty)
                    } else {
                        viewModel.startTwoPlayerGame()
                    }
                    showGameOverDialog = false
                },
                onBackToMenu = {
                    showGameOverDialog = false
                    onBackToMenu()
                }
            )
        }
    }
}

@Composable
private fun TopActionBar(
    onBackClick: () -> Unit,
    onUndoClick: () -> Unit,
    onSurrenderClick: () -> Unit,
    onPauseClick: () -> Unit,
    isPaused: Boolean,
    undoCount: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 返回按钮
        QIconButton(
            icon = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "返回菜单",
            onClick = onBackClick,
            size = 40.dp,
            backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // 暂停/继续按钮
            QIconButton(
                icon = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                contentDescription = if (isPaused) "继续" else "暂停",
                onClick = onPauseClick,
                size = 40.dp,
                backgroundColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            )

            // 悔棋按钮
            QIconButton(
                icon = Icons.AutoMirrored.Filled.Undo,
                contentDescription = "悔棋 ($undoCount)",
                onClick = onUndoClick,
                size = 40.dp,
                backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // 认输按钮
            QIconButton(
                icon = Icons.Default.Flag,
                contentDescription = "认输",
                onClick = onSurrenderClick,
                size = 40.dp,
                backgroundColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError
            )
        }
    }
}

@Composable
private fun PlayerInfoBar(
    playerName: String,
    timeLeft: String,
    isCurrentTurn: Boolean,
    isBlack: Boolean,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isCurrentTurn) {
        if (isBlack) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    val textColor = if (isCurrentTurn) {
        if (isBlack) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isCurrentTurn) 4.dp else 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 颜色指示器
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .shadow(elevation = 2.dp, shape = RoundedCornerShape(4.dp))
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            if (isBlack) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                        )
                )
                Text(
                    text = playerName,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                )
            }

            // 计时显示
            Text(
                text = timeLeft,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    fontSize = 20.sp
                )
            )
        }
    }
}

@Composable
private fun GameOverDialog(
    result: GameResult,
    onNewGame: () -> Unit,
    onBackToMenu: () -> Unit
) {
    val title = when (result) {
        GameResult.RED_WIN -> "红方胜利！"
        GameResult.BLACK_WIN -> "黑方胜利！"
        GameResult.DRAW -> "和棋！"
        GameResult.ONGOING -> ""
    }

    val emoji = when (result) {
        GameResult.RED_WIN -> "🎉"
        GameResult.BLACK_WIN -> "🎉"
        GameResult.DRAW -> "🤝"
        GameResult.ONGOING -> ""
    }

    if (title.isNotEmpty()) {
        AlertDialog(
            onDismissRequest = { },
            title = {
                Text(
                    text = "$emoji $title",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold
                    )
                )
            },
            text = { Text("游戏结束") },
            confirmButton = {
                Button(onClick = onNewGame) {
                    Text("再来一局")
                }
            },
            dismissButton = {
                Button(onClick = onBackToMenu) {
                    Text("返回菜单")
                }
            }
        )
    }
}

private fun formatTime(milliseconds: Long): String {
    val totalSeconds = milliseconds / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}
