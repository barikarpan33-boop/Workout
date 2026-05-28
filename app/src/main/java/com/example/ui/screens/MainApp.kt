package com.example.ui.screens

import android.content.Context
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.ui.NexusViewModel

@Composable
fun MainApp(viewModel: NexusViewModel, context: Context) {
    val navController = rememberNavController()
    val userStat by viewModel.userStat.collectAsStateWithLifecycle()
    
    // We navigate based on if user has accepted the quest (userStat != null)
    val startDestination = if (userStat == null) "login" else "dashboard"
    
    // Wait for the flow to emit the first time
    var isInitialized by remember { mutableStateOf(false) }
    
    LaunchedEffect(userStat) {
        if (!isInitialized) isInitialized = true
    }

    if (isInitialized) {
        NavHost(navController = navController, startDestination = startDestination) {
            composable("login") {
                LoginScreen(
                    onAccept = { name ->
                        viewModel.acceptSystemLogin(name)
                        navController.navigate("dashboard") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                )
            }
            composable("dashboard") {
                DashboardScreen(navController, viewModel)
            }
            composable("diet") {
                DietScreen(navController, viewModel)
            }
            composable("workout_list") {
                WorkoutListScreen(navController, viewModel)
            }
            composable(
                "active_workout/{presetId}",
                arguments = listOf(navArgument("presetId") { type = NavType.IntType })
            ) { backStackEntry ->
                val presetId = backStackEntry.arguments?.getInt("presetId") ?: return@composable
                ActiveWorkoutScreen(presetId, navController, viewModel)
            }
        }
    }
}
