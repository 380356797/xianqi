package com.example.myapplication.domain.game

import com.example.myapplication.data.model.ChessPiece
import com.example.myapplication.data.model.Move
import com.example.myapplication.data.model.PieceColor
import com.example.myapplication.data.model.PieceType
import com.example.myapplication.data.model.Position
import com.example.myapplication.data.model.GameResult

class ChessEngine {

    // 初始化棋盘，摆放所有棋子到初始位置
    fun initializeBoard(): List<List<ChessPiece?>> {
        val board = MutableList(10) { MutableList<ChessPiece?>(9) { null } }

        // 黑方棋子 (上方，y=0和y=1, y=2, y=3)
        // 第一排 (y=0)
        board[0][0] = ChessPiece(PieceType.CHARIOT, PieceColor.BLACK, Position(0, 0))
        board[0][1] = ChessPiece(PieceType.HORSE, PieceColor.BLACK, Position(1, 0))
        board[0][2] = ChessPiece(PieceType.ELEPHANT, PieceColor.BLACK, Position(2, 0))
        board[0][3] = ChessPiece(PieceType.ADVISOR, PieceColor.BLACK, Position(3, 0))
        board[0][4] = ChessPiece(PieceType.KING, PieceColor.BLACK, Position(4, 0))
        board[0][5] = ChessPiece(PieceType.ADVISOR, PieceColor.BLACK, Position(5, 0))
        board[0][6] = ChessPiece(PieceType.ELEPHANT, PieceColor.BLACK, Position(6, 0))
        board[0][7] = ChessPiece(PieceType.HORSE, PieceColor.BLACK, Position(7, 0))
        board[0][8] = ChessPiece(PieceType.CHARIOT, PieceColor.BLACK, Position(8, 0))

        // 炮 (y=2)
        board[2][1] = ChessPiece(PieceType.CANNON, PieceColor.BLACK, Position(1, 2))
        board[2][7] = ChessPiece(PieceType.CANNON, PieceColor.BLACK, Position(7, 2))

        // 卒 (y=3)
        board[3][0] = ChessPiece(PieceType.PAWN, PieceColor.BLACK, Position(0, 3))
        board[3][2] = ChessPiece(PieceType.PAWN, PieceColor.BLACK, Position(2, 3))
        board[3][4] = ChessPiece(PieceType.PAWN, PieceColor.BLACK, Position(4, 3))
        board[3][6] = ChessPiece(PieceType.PAWN, PieceColor.BLACK, Position(6, 3))
        board[3][8] = ChessPiece(PieceType.PAWN, PieceColor.BLACK, Position(8, 3))

        // 红方棋子 (下方，y=6, y=7, y=9)
        // 兵 (y=6)
        board[6][0] = ChessPiece(PieceType.PAWN, PieceColor.RED, Position(0, 6))
        board[6][2] = ChessPiece(PieceType.PAWN, PieceColor.RED, Position(2, 6))
        board[6][4] = ChessPiece(PieceType.PAWN, PieceColor.RED, Position(4, 6))
        board[6][6] = ChessPiece(PieceType.PAWN, PieceColor.RED, Position(6, 6))
        board[6][8] = ChessPiece(PieceType.PAWN, PieceColor.RED, Position(8, 6))

        // 炮 (y=7)
        board[7][1] = ChessPiece(PieceType.CANNON, PieceColor.RED, Position(1, 7))
        board[7][7] = ChessPiece(PieceType.CANNON, PieceColor.RED, Position(7, 7))

        // 最后一排 (y=9)
        board[9][0] = ChessPiece(PieceType.CHARIOT, PieceColor.RED, Position(0, 9))
        board[9][1] = ChessPiece(PieceType.HORSE, PieceColor.RED, Position(1, 9))
        board[9][2] = ChessPiece(PieceType.ELEPHANT, PieceColor.RED, Position(2, 9))
        board[9][3] = ChessPiece(PieceType.ADVISOR, PieceColor.RED, Position(3, 9))
        board[9][4] = ChessPiece(PieceType.KING, PieceColor.RED, Position(4, 9))
        board[9][5] = ChessPiece(PieceType.ADVISOR, PieceColor.RED, Position(5, 9))
        board[9][6] = ChessPiece(PieceType.ELEPHANT, PieceColor.RED, Position(6, 9))
        board[9][7] = ChessPiece(PieceType.HORSE, PieceColor.RED, Position(7, 9))
        board[9][8] = ChessPiece(PieceType.CHARIOT, PieceColor.RED, Position(8, 9))

        return board
    }

    // 获取所有合法走法
    fun getValidMoves(board: List<List<ChessPiece?>>, color: PieceColor): List<Move> {
        val moves = mutableListOf<Move>()
        for (y in 0..9) {
            for (x in 0..8) {
                val piece = board[y][x]
                if (piece != null && piece.color == color && piece.isAlive) {
                    moves.addAll(getValidMovesForPiece(board, piece))
                }
            }
        }
        return moves
    }

    // 获取单个棋子的合法走法
    fun getValidMovesForPiece(board: List<List<ChessPiece?>>, piece: ChessPiece): List<Move> {
        return when (piece.type) {
            PieceType.KING -> getKingMoves(board, piece)
            PieceType.ADVISOR -> getAdvisorMoves(board, piece)
            PieceType.ELEPHANT -> getElephantMoves(board, piece)
            PieceType.HORSE -> getHorseMoves(board, piece)
            PieceType.CHARIOT -> getChariotMoves(board, piece)
            PieceType.CANNON -> getCannonMoves(board, piece)
            PieceType.PAWN -> getPawnMoves(board, piece)
        }
    }

    // 验证走法是否合法
    fun isMoveValid(board: List<List<ChessPiece?>>, move: Move): Boolean {
        val validMoves = getValidMovesForPiece(board, move.movedPiece)
        return validMoves.any { it.from == move.from && it.to == move.to }
    }

    // 执行走法，返回新的棋盘状态
    fun makeMove(board: List<List<ChessPiece?>>, move: Move): List<List<ChessPiece?>> {
        val newBoard = board.map { it.toMutableList() }
        // 移动棋子
        newBoard[move.from.y][move.from.x] = null
        newBoard[move.to.y][move.to.x] = move.movedPiece.copy(position = move.to)
        return newBoard
    }

    // 判断是否将军
    fun isInCheck(board: List<List<ChessPiece?>>, kingColor: PieceColor): Boolean {
        val kingPosition = findKingPosition(board, kingColor) ?: return false
        val opponentColor = if (kingColor == PieceColor.RED) PieceColor.BLACK else PieceColor.RED
        val opponentMoves = getValidMoves(board, opponentColor)
        return opponentMoves.any { it.to == kingPosition }
    }

    // 判断是否将死
    fun isCheckmate(board: List<List<ChessPiece?>>, kingColor: PieceColor): Boolean {
        if (!isInCheck(board, kingColor)) return false
        return getValidMoves(board, kingColor).isEmpty()
    }

    // 判断是否困毙（无子可动但未被将军）
    fun isStalemate(board: List<List<ChessPiece?>>, color: PieceColor): Boolean {
        if (isInCheck(board, color)) return false
        return getValidMoves(board, color).isEmpty()
    }

    // 判断游戏结果
    fun getGameResult(board: List<List<ChessPiece?>>, currentTurn: PieceColor): GameResult {
        val nextTurn = if (currentTurn == PieceColor.RED) PieceColor.BLACK else PieceColor.RED
        return when {
            isCheckmate(board, PieceColor.BLACK) -> GameResult.RED_WIN
            isCheckmate(board, PieceColor.RED) -> GameResult.BLACK_WIN
            isStalemate(board, nextTurn) -> GameResult.DRAW
            else -> GameResult.ONGOING
        }
    }

    // 寻找将/帅的位置
    private fun findKingPosition(board: List<List<ChessPiece?>>, color: PieceColor): Position? {
        for (y in 0..9) {
            for (x in 0..8) {
                val piece = board[y][x]
                if (piece?.type == PieceType.KING && piece.color == color) {
                    return piece.position
                }
            }
        }
        return null
    }

    // 各个棋子的走法实现
    private fun getKingMoves(board: List<List<ChessPiece?>>, king: ChessPiece): List<Move> {
        val moves = mutableListOf<Move>()
        val (x, y) = king.position
        val directions = listOf(-1 to 0, 1 to 0, 0 to -1, 0 to 1)

        val palaceXRange = 3..5
        val palaceYRange = if (king.color == PieceColor.RED) 7..9 else 0..2

        for ((dx, dy) in directions) {
            val newX = x + dx
            val newY = y + dy
            if (newX in palaceXRange && newY in palaceYRange) {
                val targetPiece = board[newY][newX]
                if (targetPiece == null || targetPiece.color != king.color) {
                    moves.add(Move(
                        from = king.position,
                        to = Position(newX, newY),
                        movedPiece = king,
                        capturedPiece = targetPiece
                    ))
                }
            }
        }

        // 将帅对面规则
        val opponentColor = if (king.color == PieceColor.RED) PieceColor.BLACK else PieceColor.RED
        val opponentKing = findKingPosition(board, opponentColor)
        if (opponentKing != null && opponentKing.x == x) {
            var hasObstacle = false
            val minY = minOf(y, opponentKing.y)
            val maxY = maxOf(y, opponentKing.y)
            for (checkY in minY + 1 until maxY) {
                if (board[checkY][x] != null) {
                    hasObstacle = true
                    break
                }
            }
            if (!hasObstacle) {
                moves.add(Move(
                    from = king.position,
                    to = opponentKing,
                    movedPiece = king,
                    capturedPiece = board[opponentKing.y][opponentKing.x]
                ))
            }
        }

        return moves
    }

    private fun getAdvisorMoves(board: List<List<ChessPiece?>>, advisor: ChessPiece): List<Move> {
        val moves = mutableListOf<Move>()
        val (x, y) = advisor.position
        val directions = listOf(-1 to -1, -1 to 1, 1 to -1, 1 to 1)

        val palaceXRange = 3..5
        val palaceYRange = if (advisor.color == PieceColor.RED) 7..9 else 0..2

        for ((dx, dy) in directions) {
            val newX = x + dx
            val newY = y + dy
            if (newX in palaceXRange && newY in palaceYRange) {
                val targetPiece = board[newY][newX]
                if (targetPiece == null || targetPiece.color != advisor.color) {
                    moves.add(Move(
                        from = advisor.position,
                        to = Position(newX, newY),
                        movedPiece = advisor,
                        capturedPiece = targetPiece
                    ))
                }
            }
        }
        return moves
    }

    private fun getElephantMoves(board: List<List<ChessPiece?>>, elephant: ChessPiece): List<Move> {
        val moves = mutableListOf<Move>()
        val (x, y) = elephant.position
        val directions = listOf(-2 to -2, -2 to 2, 2 to -2, 2 to 2)
        val obstacleOffsets = listOf(-1 to -1, -1 to 1, 1 to -1, 1 to 1)

        val riverY = 4 // 楚河汉界在y=4和y=5之间

        for (i in directions.indices) {
            val (dx, dy) = directions[i]
            val (obstacleDx, obstacleDy) = obstacleOffsets[i]
            val newX = x + dx
            val newY = y + dy
            val obstacleX = x + obstacleDx
            val obstacleY = y + obstacleDy

            if (newX in 0..8 && newY in 0..9) {
                // 象不能过河
                if ((elephant.color == PieceColor.RED && newY <= riverY) ||
                    (elephant.color == PieceColor.BLACK && newY >= riverY + 1)) {
                    continue
                }

                // 象眼不能被塞
                if (board[obstacleY][obstacleX] != null) {
                    continue
                }

                val targetPiece = board[newY][newX]
                if (targetPiece == null || targetPiece.color != elephant.color) {
                    moves.add(Move(
                        from = elephant.position,
                        to = Position(newX, newY),
                        movedPiece = elephant,
                        capturedPiece = targetPiece
                    ))
                }
            }
        }
        return moves
    }

    private fun getHorseMoves(board: List<List<ChessPiece?>>, horse: ChessPiece): List<Move> {
        val moves = mutableListOf<Move>()
        val (x, y) = horse.position
        val directions = listOf(
            -2 to -1 to (-1 to 0),  // 左上
            -2 to 1 to (-1 to 0),   // 左下
            2 to -1 to (1 to 0),    // 右上
            2 to 1 to (1 to 0),     // 右下
            -1 to -2 to (0 to -1),  // 上左
            1 to -2 to (0 to -1),   // 上右
            -1 to 2 to (0 to 1),    // 下左
            1 to 2 to (0 to 1)      // 下右
        )

        for ((moveDir, obstacleDir) in directions) {
            val (dx, dy) = moveDir
            val (obstacleDx, obstacleDy) = obstacleDir
            val newX = x + dx
            val newY = y + dy
            val obstacleX = x + obstacleDx
            val obstacleY = y + obstacleDy

            if (newX in 0..8 && newY in 0..9) {
                // 马腿不能被别
                if (board[obstacleY][obstacleX] != null) {
                    continue
                }

                val targetPiece = board[newY][newX]
                if (targetPiece == null || targetPiece.color != horse.color) {
                    moves.add(Move(
                        from = horse.position,
                        to = Position(newX, newY),
                        movedPiece = horse,
                        capturedPiece = targetPiece
                    ))
                }
            }
        }
        return moves
    }

    private fun getChariotMoves(board: List<List<ChessPiece?>>, chariot: ChessPiece): List<Move> {
        val moves = mutableListOf<Move>()
        val (x, y) = chariot.position
        val directions = listOf(-1 to 0, 1 to 0, 0 to -1, 0 to 1)

        for ((dx, dy) in directions) {
            var newX = x + dx
            var newY = y + dy
            while (newX in 0..8 && newY in 0..9) {
                val targetPiece = board[newY][newX]
                if (targetPiece == null) {
                    moves.add(Move(
                        from = chariot.position,
                        to = Position(newX, newY),
                        movedPiece = chariot
                    ))
                } else {
                    if (targetPiece.color != chariot.color) {
                        moves.add(Move(
                            from = chariot.position,
                            to = Position(newX, newY),
                            movedPiece = chariot,
                            capturedPiece = targetPiece
                        ))
                    }
                    break // 遇到棋子停止
                }
                newX += dx
                newY += dy
            }
        }
        return moves
    }

    private fun getCannonMoves(board: List<List<ChessPiece?>>, cannon: ChessPiece): List<Move> {
        val moves = mutableListOf<Move>()
        val (x, y) = cannon.position
        val directions = listOf(-1 to 0, 1 to 0, 0 to -1, 0 to 1)

        for ((dx, dy) in directions) {
            var newX = x + dx
            var newY = y + dy
            var hasPlatform = false

            while (newX in 0..8 && newY in 0..9) {
                val targetPiece = board[newY][newX]
                if (targetPiece == null) {
                    if (!hasPlatform) {
                        moves.add(Move(
                            from = cannon.position,
                            to = Position(newX, newY),
                            movedPiece = cannon
                        ))
                    }
                } else {
                    if (!hasPlatform) {
                        hasPlatform = true
                    } else {
                        if (targetPiece.color != cannon.color) {
                            moves.add(Move(
                                from = cannon.position,
                                to = Position(newX, newY),
                                movedPiece = cannon,
                                capturedPiece = targetPiece
                            ))
                        }
                        break // 第二个棋子，无论敌我都停止
                    }
                }
                newX += dx
                newY += dy
            }
        }
        return moves
    }

    private fun getPawnMoves(board: List<List<ChessPiece?>>, pawn: ChessPiece): List<Move> {
        val moves = mutableListOf<Move>()
        val (x, y) = pawn.position
        val isRed = pawn.color == PieceColor.RED

        // 前进方向
        val forwardDy = if (isRed) -1 else 1
        val riverY = 4 // 楚河汉界在y=4和y=5之间

        // 前进一步
        val newY = y + forwardDy
        if (newY in 0..9) {
            val targetPiece = board[newY][x]
            if (targetPiece == null || targetPiece.color != pawn.color) {
                moves.add(Move(
                    from = pawn.position,
                    to = Position(x, newY),
                    movedPiece = pawn,
                    capturedPiece = targetPiece
                ))
            }
        }

        // 过河后可以左右走
        val hasCrossedRiver = if (isRed) y <= riverY else y >= riverY + 1
        if (hasCrossedRiver) {
            for (dx in listOf(-1, 1)) {
                val newX = x + dx
                if (newX in 0..8) {
                    val targetPiece = board[y][newX]
                    if (targetPiece == null || targetPiece.color != pawn.color) {
                        moves.add(Move(
                            from = pawn.position,
                            to = Position(newX, y),
                            movedPiece = pawn,
                            capturedPiece = targetPiece
                        ))
                    }
                }
            }
        }

        return moves
    }
}
