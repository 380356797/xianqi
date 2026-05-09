package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.screens.GameScreen
import com.example.myapplication.ui.screens.MenuScreen
import com.example.myapplication.ui.screens.SettingsScreen
import com.example.myapplication.ui.screens.StatsScreen
import com.example.myapplication.ui.theme.ChessQTheme
import com.example.myapplication.ui.viewmodel.AppViewModelFactory
import com.example.myapplication.ui.viewmodel.GameViewModel
import com.example.myapplication.ui.viewmodel.SettingsViewModel
import com.example.myapplication.ui.viewmodel.StatsViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ChessQTheme {
                ChessApp()
            }
        }
    }
}

@Composable
fun ChessApp(
    navController: NavHostController = rememberNavController(),
    gameViewModel: GameViewModel = viewModel(),
    settingsViewModel: SettingsViewModel = viewModel(factory = AppViewModelFactory(LocalContext.current)),
    statsViewModel: StatsViewModel = viewModel(factory = AppViewModelFactory(LocalContext.current))
) {
    var showDifficultyDialog by remember { mutableStateOf(false) }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "menu",
            modifier = Modifier.padding(innerPadding)
        ) {
            // 主菜单页面
            composable("menu") {
                MenuScreen(
                    onStartTwoPlayerGame = {
                        gameViewModel.startTwoPlayerGame()
                        navController.navigate("game")
                    },
                    onStartSinglePlayerGame = {
                        showDifficultyDialog = true
                    },
                    onOpenSettings = {
                        navController.navigate("settings")
                    },
                    onOpenStats = {
                        navController.navigate("stats")
                    }
                )
            }

            // 游戏页面
            composable("game") {
                GameScreen(
                    viewModel = gameViewModel,
                    onBackToMenu = {
                        navController.popBackStack("menu", inclusive = false)
                    }
                )
            }

            // 设置页面
            composable("settings") {
                SettingsScreen(
                    viewModel = settingsViewModel,
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }

            // 统计页面
            composable("stats") {
                StatsScreen(
                    viewModel = statsViewModel,
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }

    // 单人对战难度选择弹窗
    if (showDifficultyDialog) {
        DifficultySelectionDialog(
            onDifficultySelected = { difficulty ->
                gameViewModel.startAiGame(aiDifficulty = difficulty)
                showDifficultyDialog = false
                navController.navigate("game")
            },
            onDismiss = {
                showDifficultyDialog = false
            }
        )
    }
}

@Composable
fun DifficultySelectionDialog(
    onDifficultySelected: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("选择难度") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { onDifficultySelected(1) },
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text("简单 - 适合新手")
                }
                Spacer(modifier = Modifier.height(4.dp))
                Button(
                    onClick = { onDifficultySelected(2) },
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text("中等 - 有一定挑战")
                }
                Spacer(modifier = Modifier.height(4.dp))
                Button(
                    onClick = { onDifficultySelected(3) },
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text("困难 - 棋力较量")
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}