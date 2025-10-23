package com.mytracker.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.mytracker.app.ui.activegoal.ActiveGoalScreen
import com.mytracker.app.ui.creategoal.CreateGoalScreen
import com.mytracker.app.ui.templateselection.TemplateSelectionScreen

sealed class Screen(val route: String) {
    object TemplateSelection : Screen("template_selection")
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
        // Экран выбора шаблона
        composable(Screen.TemplateSelection.route) {
            TemplateSelectionScreen(
                onGoalCreated = { goalId ->
                    navController.navigate(Screen.ActiveGoal.route) {
                        popUpTo(Screen.TemplateSelection.route) { inclusive = true }
                    }
                },
                onCustomGoalClick = {
                    navController.navigate(Screen.CreateGoal.route)
                }
            )
        }
        
        // Экран создания своей цели
        composable(Screen.CreateGoal.route) {
            CreateGoalScreen(
                onGoalCreated = {
                    navController.navigate(Screen.ActiveGoal.route) {
                        popUpTo(Screen.TemplateSelection.route) { inclusive = true }
                    }
                },
                onBackPressed = {
                    navController.popBackStack()
                }
            )
        }
        
        // Экран активной цели
        composable(Screen.ActiveGoal.route) {
            ActiveGoalScreen(
                onNoGoal = {
                    navController.navigate(Screen.TemplateSelection.route) {
                        popUpTo(Screen.ActiveGoal.route) { inclusive = true }
                    }
                }
            )
        }
    }
}

