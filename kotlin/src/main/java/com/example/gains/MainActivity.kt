package com.example.gains

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.gains.home.HomeScreen
import com.example.gains.onboarding.Login
import com.example.gains.onboarding.NewAccount1
import com.example.gains.onboarding.NewAccount2
import com.example.gains.onboarding.ResetPassword
import com.example.gains.ui.theme.GainsTheme
import com.google.firebase.FirebaseApp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable

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
                    composable("HomeScreen") {
                        HomeScreen(navController = navController)
                    }
                }
            }
        }
    }
}