package com.example.myapplication.ui.theme

import androidx.compose.ui.graphics.Color

// Q版主题主色调 - 暖橙色系
val Primary = Color(0xFFFF9800)          // 主色：明亮橙色
val PrimaryDark = Color(0xFFF57C00)      // 深色主色
val PrimaryLight = Color(0xFFFFB74D)     // 浅色主色

// 辅助色 - 天蓝色系
val Secondary = Color(0xFF29B6F6)        // 辅助色：天蓝色
val SecondaryDark = Color(0xFF0288D1)    // 深色辅助色
val SecondaryLight = Color(0xFF81D4FA)   // 浅色辅助色

// 基础色
val Background = Color(0xFFFFF8E1)       // 背景色：浅米色，温暖柔和
val Surface = Color(0xFFFFF3E0)          // 表面色：比背景稍深一点
val SurfaceVariant = Color(0xFFFFE0B2)   // 变体表面色
val Error = Color(0xFFE53935)            // 错误色：红色

// 文字颜色
val TextPrimary = Color(0xFF212121)      // 主要文字：深灰色
val TextSecondary = Color(0xFF757575)    // 次要文字：中灰色
val TextOnPrimary = Color.White           // 主色上的文字：白色
val TextOnSecondary = Color.White         // 辅助色上的文字：白色

// 象棋相关颜色
val RedPiece = Color(0xFFE53935)         // 红方棋子：鲜亮红色
val BlackPiece = Color(0xFF424242)       // 黑方棋子：深灰色（纯黑太刺眼）
val BoardBackground = Color(0xFFA1887F)  // 棋盘背景：浅棕色木纹色
val BoardLine = Color(0xFF5D4037)        // 棋盘线条：深棕色
val PossibleMoveSpot = Color(0x994CAF50) // 可走位置提示：半透明绿色
val SelectedPieceBorder = Color(0xFFFFEB3B) // 选中棋子边框：黄色

// 深色模式颜色
val DarkBackground = Color(0xFF121212)
val DarkSurface = Color(0xFF1E1E1E)
val DarkTextPrimary = Color.White
val DarkTextSecondary = Color(0xFFB0BEC5)