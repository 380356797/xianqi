package com.example.myapplication.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.model.ChessPiece
import com.example.myapplication.data.model.Move
import com.example.myapplication.data.model.Position
import com.example.myapplication.ui.theme.BoardBackground
import com.example.myapplication.ui.theme.BoardLine
import com.example.myapplication.ui.theme.PossibleMoveSpot
import kotlin.math.roundToInt

/**
 * 象棋棋盘组件
 */
@Composable
fun ChessBoard(
    board: List<List<ChessPiece?>>,
    validMoves: List<Move> = emptyList(),
    onMoveMade: (Move) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedPosition by remember { mutableStateOf<Position?>(null) }
    val density = LocalDensity.current

    // 计算当前选中棋子的可走位置
    val possibleMoves = remember(selectedPosition, validMoves) {
        validMoves.filter { it.from == selectedPosition }
    }

    BoxWithConstraints(
        modifier = modifier
            .aspectRatio(9f / 10f)
            .background(BoardBackground)
            .padding(8.dp)
    ) {
        // 根据实际尺寸动态计算格子大小
        val actualCellWidth = maxWidth / 9
        val actualCellHeight = maxHeight / 10
        val cellSize = if (actualCellWidth < actualCellHeight) actualCellWidth else actualCellHeight

        with(density) {
            val cellSizePx = cellSize.toPx()

            // 绘制棋盘网格
            ChessBoardGrid(modifier = Modifier.fillMaxSize())

            // 绘制可走位置提示
            possibleMoves.forEach { move ->
                val targetPiece = board[move.to.y][move.to.x]
                // 如果目标位置没有棋子，绘制可走位置提示并添加点击事件
                if (targetPiece == null) {
                    Box(
                        modifier = Modifier
                            .offset(
                                x = (move.to.x * cellSize.value).dp,
                                y = (move.to.y * cellSize.value).dp
                            )
                            .size(cellSize),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(
                            modifier = Modifier
                                .size(cellSize * 0.3f)
                        ) {
                            drawCircle(
                                color = PossibleMoveSpot,
                                radius = cellSizePx * 0.15f
                            )
                        }
                        // 添加点击事件覆盖层
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .pointerInput(Unit) {
                                    detectTapGestures {
                                        possibleMoves.firstOrNull { it.to == move.to }?.let { m ->
                                            onMoveMade(m)
                                            selectedPosition = null
                                        }
                                    }
                                }
                        )
                    }
                }
            }

            // 绘制棋子
            board.forEachIndexed { y, row ->
                row.forEachIndexed { x, piece ->
                    piece?.let {
                        val position = Position(x, y)
                        val isSelected = selectedPosition == position
                        val isPossibleTarget = possibleMoves.any { it.to == position }

                        ChessPieceView(
                            piece = piece,
                            isSelected = isSelected,
                            isPossibleMove = isPossibleTarget,
                            onClick = {
                                if (isPossibleTarget) {
                                    possibleMoves.firstOrNull { it.to == position }?.let { move ->
                                        onMoveMade(move)
                                        selectedPosition = null
                                    }
                                } else {
                                    selectedPosition = if (selectedPosition == position) null else position
                                }
                            },
                            modifier = Modifier
                                .offset(
                                    x = (x * cellSize.value).dp,
                                    y = (y * cellSize.value).dp
                                )
                                .size(cellSize)
                        )
                    }
                }
            }

        }
    }
}

/**
 * 棋盘网格绘制
 */
@Composable
private fun ChessBoardGrid(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val cellWidth = width / 8f
        val cellHeight = height / 9f

        // 绘制横线：10条
        for (i in 0..9) {
            drawLine(
                color = BoardLine,
                start = Offset(0f, i * cellHeight),
                end = Offset(width, i * cellHeight),
                strokeWidth = 2.dp.toPx()
            )
        }

        // 绘制竖线：9条
        for (i in 0..8) {
            drawLine(
                color = BoardLine,
                start = Offset(i * cellWidth, 0f),
                end = Offset(i * cellWidth, height),
                strokeWidth = 2.dp.toPx()
            )
        }

        // 绘制九宫格斜线 - 红方（下方）
        drawNinePalaceLines(
            isRed = true,
            cellWidth = cellWidth,
            cellHeight = cellHeight
        )

        // 绘制九宫格斜线 - 黑方（上方）
        drawNinePalaceLines(
            isRed = false,
            cellWidth = cellWidth,
            cellHeight = cellHeight
        )

        // 绘制楚河汉界装饰
        drawRiverDecoration(width, height, cellHeight)
    }
}

/**
 * 绘制九宫格斜线
 */
private fun DrawScope.drawNinePalaceLines(
    isRed: Boolean,
    cellWidth: Float,
    cellHeight: Float
) {
    val baseY = if (isRed) 7 * cellHeight else 0f // 红方九宫在7-9行，黑方在0-2行
    val leftX = 3 * cellWidth // 九宫左边界
    val rightX = 5 * cellWidth // 九宫右边界
    val topY = baseY
    val bottomY = baseY + 2 * cellHeight

    // 左上到右下斜线
    drawLine(
        color = BoardLine,
        start = Offset(leftX, topY),
        end = Offset(rightX, bottomY),
        strokeWidth = 2.dp.toPx()
    )

    // 右上到左下斜线
    drawLine(
        color = BoardLine,
        start = Offset(rightX, topY),
        end = Offset(leftX, bottomY),
        strokeWidth = 2.dp.toPx()
    )
}

/**
 * 绘制楚河汉界装饰
 */
private fun DrawScope.drawRiverDecoration(
    width: Float,
    height: Float,
    cellHeight: Float
) {
    val riverTopY = 4 * cellHeight
    val riverBottomY = 5 * cellHeight
    val riverCenterY = (riverTopY + riverBottomY) / 2

    // 绘制装饰性波浪线或文字，这里简化为两条细虚线
    val pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 10f), 0f)
    drawLine(
        color = BoardLine.copy(alpha = 0.3f),
        start = Offset(width * 0.1f, riverCenterY - 5.dp.toPx()),
        end = Offset(width * 0.9f, riverCenterY - 5.dp.toPx()),
        strokeWidth = 1.dp.toPx(),
        pathEffect = pathEffect
    )
    drawLine(
        color = BoardLine.copy(alpha = 0.3f),
        start = Offset(width * 0.1f, riverCenterY + 5.dp.toPx()),
        end = Offset(width * 0.9f, riverCenterY + 5.dp.toPx()),
        strokeWidth = 1.dp.toPx(),
        pathEffect = pathEffect
    )
}
