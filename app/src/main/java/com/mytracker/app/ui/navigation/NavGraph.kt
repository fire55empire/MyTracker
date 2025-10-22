package com.mytracker.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.mytracker.app.ui.activegoal.ActiveGoalScreen
import com.mytracker.app.ui.creategoal.CreateGoalScreen

sealed class Screen(val route: String) {
    object CreateGoal : Screen("create_goal")
    object ActiveGoal : Screen("active_goal")
}

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.CreateGoal.route) {
            CreateGoalScreen(
                onGoalCreated = {
                    navController.navigate(Screen.ActiveGoal.route) {
                        popUpTo(Screen.CreateGoal.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.ActiveGoal.route) {
            ActiveGoalScreen(
                onNoGoal = {
                    navController.navigate(Screen.CreateGoal.route) {
                        popUpTo(Screen.ActiveGoal.route) { inclusive = true }
                    }
                }
            )
        }
    }
}

