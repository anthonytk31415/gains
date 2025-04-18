package com.example.gains.home.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.gains.R
import com.example.gains.home.AddScreen
import com.example.gains.home.exercise.ExerciseDetailsScreen
import com.example.gains.home.HomeScreen
import com.example.gains.home.ProfileScreen
import com.example.gains.home.SettingsScreen
import com.example.gains.home.ViewScreen
import com.example.gains.home.exercise.Exercise

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeNavGraph(
    mainNavController: NavHostController,
    username: String
) {
    val homeNavController = rememberNavController()
    val navItems = listOf(
        BottomNav.Home,
        BottomNav.Add,
        BottomNav.View,
        BottomNav.Profile,
        BottomNav.Settings
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("gAIns- $username") }
            )
        },
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by homeNavController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                navItems.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        selected = currentRoute == item.route,
                        onClick = {
                            homeNavController.navigate(item.route) {
                                popUpTo(homeNavController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = homeNavController,
            startDestination = BottomNav.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNav.Home.route) {  HomeScreen(navController = homeNavController) }
            composable(BottomNav.Add.route) { AddScreen(navController = homeNavController) }
            composable(BottomNav.View.route) { ViewScreen(navController = homeNavController) }
            composable(BottomNav.Profile.route) { ProfileScreen(navController = homeNavController) }
            composable(BottomNav.Settings.route) { SettingsScreen(navController = homeNavController) }
            composable(
                route = "exerciseDetail/{exerciseId}",
                arguments = listOf(navArgument("exerciseId") { type = NavType.StringType })
            ) { backStackEntry ->
                val exerciseId = backStackEntry.arguments?.getString("exerciseId") ?: ""

                val exercise = when (exerciseId) {
                    "leg_raises" -> Exercise(
                        name = "Leg Raises",
                        imageRes = R.drawable.leg_raises,
                        videoUrl = "https://www.youtube.com/watch?v=jBhZWX91bec",
                        instructions = "Lie flat and raise legs...",
                        breathingTips = "Exhale while lifting legs.",
                        commonMistakes = "Don't arch the back."
                    )
                    "push_ups" -> Exercise(
                        name = "Push Ups",
                        imageRes = R.drawable.push_ups,
                        videoUrl = "https://www.youtube.com/watch?v=jBhZWX91bec",
                        instructions = "Keep your core tight...",
                        breathingTips = "Inhale down, exhale up.",
                        commonMistakes = "Avoid flared elbows."
                    )
                    else -> null
                }

                exercise?.let {
                    ExerciseDetailsScreen(
                        exerciseName = it.name,
                        imageRes = it.imageRes,
                        videoUrl = it.videoUrl,
                        instructions = it.instructions,
                        breathingTips = it.breathingTips,
                        commonMistakes = it.commonMistakes
                    )
                }
            }

        }
    }
}