package com.example.myapplication.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.model.ChessPiece
import com.example.myapplication.data.model.PieceColor
import com.example.myapplication.data.model.PieceType
import com.example.myapplication.ui.theme.BlackPiece
import com.example.myapplication.ui.theme.RedPiece
import com.example.myapplication.ui.theme.SelectedPieceBorder

/**
 * 象棋棋子组件
 */
@Composable
fun ChessPieceView(
    piece: ChessPiece,
    isSelected: Boolean = false,
    isPossibleMove: Boolean = false,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    size: Dp = 40.dp
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val baseColor = if (piece.color == PieceColor.RED) RedPiece else BlackPiece
    val textColor = Color.White
    val pieceText = getPieceText(piece.type, piece.color)

    val shadowElevation = when {
        isPressed -> 2.dp
        isSelected -> 8.dp
        else -> 4.dp
    }

    val borderWidth = if (isSelected) 3.dp else 2.dp
    val borderColor = if (isSelected) SelectedPieceBorder else Color(0xFFFFD54F) // 默认金色边框

    Box(
        modifier = modifier
            .size(size)
            .shadow(elevation = shadowElevation, shape = CircleShape)
            .clip(CircleShape)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        baseColor,
                        baseColor.copy(alpha = 0.8f)
                    )
                )
            )
            .border(
                width = borderWidth,
                color = borderColor,
                shape = CircleShape
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = pieceText,
            color = textColor,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                fontSize = (size.value * 0.4f).sp
            )
        )

        if (isPossibleMove) {
            Canvas(modifier = Modifier.size(size)) {
                drawCircle(
                    color = Color.Green.copy(alpha = 0.7f),
                    radius = size.toPx() * 0.3f,
                    style = Stroke(
                        width = 4.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                    )
                )
            }
        }
    }
}

/**
 * 空棋子位置，用于显示可走点提示
 */
@Composable
fun EmptyChessSpot(
    isPossibleMove: Boolean = false,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    size: Dp = 40.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (isPossibleMove) {
            Canvas(modifier = Modifier.size(size * 0.4f)) {
                drawRoundRect(
                    color = Color.Green.copy(alpha = 0.6f),
                    cornerRadius = CornerRadius(8.dp.toPx()),
                    size = Size(size.toPx() * 0.4f, size.toPx() * 0.4f),
                    topLeft = Offset(size.toPx() * 0.3f, size.toPx() * 0.3f)
                )
            }
        }
    }
}

/**
 * 获取棋子对应的文字
 */
private fun getPieceText(type: PieceType, color: PieceColor): String {
    return when (type) {
        PieceType.KING -> if (color == PieceColor.RED) "帅" else "将"
        PieceType.ADVISOR -> if (color == PieceColor.RED) "仕" else "士"
        PieceType.ELEPHANT -> if (color == PieceColor.RED) "相" else "象"
        PieceType.HORSE -> if (color == PieceColor.RED) "马" else "馬"
        PieceType.CHARIOT -> if (color == PieceColor.RED) "车" else "車"
        PieceType.CANNON -> if (color == PieceColor.RED) "炮" else "砲"
        PieceType.PAWN -> if (color == PieceColor.RED) "兵" else "卒"
    }
}
