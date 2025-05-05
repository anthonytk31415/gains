package com.example.gains.home.navigation

import android.content.Context
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.material3.*
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.gains.R
import com.example.gains.home.*
import com.example.gains.home.exercise.Exercise
import com.example.gains.home.exercise.loadExercisesFromJson
import com.example.gains.home.exercise.ExerciseDetailsScreen
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeNavGraph(
    mainNavController: NavHostController,
    username: String
) {
    val homeNavController = rememberNavController()
    val context = LocalContext.current

    // Load exercises once and remember
    val exercises by remember {
        mutableStateOf(loadExercisesFromJson(context))
    }

    val navItems = listOf(
        BottomNav.Home,
        BottomNav.Add,
        BottomNav.View,
        BottomNav.Profile,
        BottomNav.Settings
    )

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("gAIns - $username") })
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
                                popUpTo(homeNavController.graph.startDestinationId) { saveState = true }
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
            composable(BottomNav.Home.route) { HomeScreen(navController = homeNavController) }
            composable(BottomNav.Add.route) { AddScreen(navController = homeNavController) }
            composable(BottomNav.View.route) { ViewScreen(navController = homeNavController) }
            composable(BottomNav.Profile.route) { ProfileScreen(navController = homeNavController) }
            composable(BottomNav.Settings.route) { SettingsScreen(navController = homeNavController) }

            composable(
                route = "exerciseDetail/{exerciseId}",
                arguments = listOf(navArgument("exerciseId") { type = NavType.StringType })
            ) { backStackEntry ->
                val exerciseId = backStackEntry.arguments?.getString("exerciseId") ?: ""
                val matched = exercises.find { it.id == exerciseId }

                matched?.let {
                    val resId = context.resources.getIdentifier(it.imageResName, "drawable", context.packageName)
                    ExerciseDetailsScreen(
                        navController = homeNavController,
                        exercise = it.copy(imageRes = resId)
                    )
                }
            }
        }
    }
}


