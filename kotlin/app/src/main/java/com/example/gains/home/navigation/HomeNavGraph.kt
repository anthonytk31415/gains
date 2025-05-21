package com.example.gains.home.navigation

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.gains.R
import com.example.gains.UserSession
import com.example.gains.home.ExerciseScreen
import com.example.gains.ViewScreen
import com.example.gains.home.*
import com.example.gains.home.exercise.Exercise
import com.example.gains.home.exercise.loadExercisesFromJson
import com.example.gains.home.exercise.ExerciseDetailsScreen
import com.example.gains.home.model.WorkoutRoutine
import com.example.gains.home.network.CreationService.createUserAccount
import com.example.gains.home.network.LoginService.loginUser
import com.example.gains.home.network.WorkoutApi
import com.example.gains.home.network.WorkoutService
import com.example.gains.home.model.WorkoutViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject

@SuppressLint("ContextCastToActivity")
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeNavGraph(
    mainNavController: NavHostController,
    username: String,
    email: String,
    isNewAccount: Boolean
) {
    val homeNavController = rememberNavController()
    val isLoaded = remember { mutableStateOf(false) }
    val workout = remember { mutableStateOf<WorkoutRoutine?>(null) }
    val context = LocalContext.current
    val workoutViewModel: WorkoutViewModel = viewModel(LocalContext.current as ComponentActivity)//May cause issues in future
    Log.d("HomeNavGraph", "isNewAccount: $isNewAccount")
    LaunchedEffect(isNewAccount) {
        // When it's a new account, fetch data after creating the user
        if (isNewAccount) {
            try {
                // Assume we create a user account here, get the userId and fetch workout data
                Log.d("HomeNavGraph", "isNewAccount: $isNewAccount")
                val response = createUserAccount(email)
                val json = JSONObject(response)
                val userId = json.getInt("user_id")
                UserSession.userId = userId
                isLoaded.value = true
            } catch (e: Exception) {
                println("Error creating user or fetching workout: ${e.message}")
                isLoaded.value = true
            }
        } else {
            // For existing users, load data from UserSession
            try{
                val response = loginUser(email)
                Log.d("HomeNavGraph", "Response: $response")
                val jsonResponse = JSONObject(response)
                val userId = jsonResponse.getInt("user_id")
                Log.d("HomeNavGraph", "User ID: $userId")
                UserSession.userId = userId
                Log.d("UserSession", "UserSession.userId = ${UserSession.userId}") // log from object
                isLoaded.value = true
            } catch (e: Exception) {
                println("Error creating user or fetching workout: ${e.message}")
                isLoaded.value = true
            }

        }
    }

    // Load exercises once and remember
    val exercises by remember {
        mutableStateOf(loadExercisesFromJson(context))
    }

    val navItems = listOf(
        BottomNav.Home,
        BottomNav.View,
        BottomNav.Add,
        BottomNav.Profile,
        BottomNav.Exercises
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("gAIns - $username") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    TextButton(
                        onClick = {
                            mainNavController.navigate("Login") {
                                popUpTo(0) { inclusive = true } // Clear backstack
                            }
                        }
                    ) {
                        Text(
                            text = "Logout",
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
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
        if(isLoaded.value) {
            NavHost(
                navController = homeNavController,
                startDestination = BottomNav.Home.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(BottomNav.Home.route) { HomeScreen(navController = homeNavController) }
                composable(BottomNav.Add.route) {
                    AddScreen(
                        navController = homeNavController,
                        workoutViewModel = workoutViewModel
                    )
                }
                composable(BottomNav.View.route) { ViewScreen(navController = homeNavController) }
                composable(BottomNav.Profile.route) { ProfileScreen(navController = homeNavController) }
                composable(BottomNav.Exercises.route) { ExerciseScreen(navController = homeNavController) }
                composable("generatedWorkout") {
                    GeneratedWorkoutScreen(
                        navController = homeNavController,
                        workoutViewModel = workoutViewModel
                    )
                }


                composable(
                    route = "exerciseDetail/{exerciseId}",
                    arguments = listOf(navArgument("exerciseId") { type = NavType.IntType }) // Use IntType here
                ) { backStackEntry ->

                    val exerciseId = backStackEntry.arguments?.getInt("exerciseId")
//                    Log.d("Debug", "exerciseId type: ${exerciseId?.javaClass?.name}")
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
        else{
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}