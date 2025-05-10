package com.example.gains

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import com.example.gains.ui.theme.GainsTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.google.firebase.FirebaseApp

//Navigation paths
import com.example.gains.home.HomeScreen
import com.example.gains.onboarding.Login
import com.example.gains.onboarding.NewAccount1
import com.example.gains.onboarding.NewAccount2
import com.example.gains.onboarding.ResetPassword
import com.example.gains.home.navigation.HomeNavGraph





class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()
        setContent {
            GainsTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "Login") {
                    composable("Login") {
                        Login(navController = navController)
                    }
                    composable("NewAccount1") {
                        NewAccount1(navController = navController)
                    }
                    composable(
                        route = "NewAccount2/{email}",
                        arguments = listOf(navArgument("email") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val email = backStackEntry.arguments?.getString("email") ?: ""
                        NewAccount2(navController = navController, email = email)
                    }
                    composable("ResetPassword") {
                        ResetPassword(navController = navController)
                    }
                    composable(
                        route = "HomeNav/{isNewAccount}",
                        arguments = listOf(navArgument("isNewAccount") { type = NavType.StringType })
                    )   { backStackEntry ->
                        val isNewAccount = backStackEntry.arguments?.getString("isNewAccount")?.toBoolean() ?: false

                        HomeNavGraph(
                            mainNavController = navController,
                            username = UserSession.username ?: "Unknown",
                            email = UserSession.email ?: "Unknown",
                            isNewAccount = isNewAccount
                        )
                    }
                }
            }
        }
    }
}