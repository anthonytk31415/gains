package com.example.gains

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.gains.ui.theme.MyApplicationTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import com.example.gains.home.HomeScreen
import com.example.gains.onboarding.Login
import com.example.gains.onboarding.NewAccount1
import com.example.gains.onboarding.NewAccount2
import com.example.gains.onboarding.ResetPassword

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "Login") {
                    composable("Login") {
                        Login(navController = navController)
                    }
                    composable("NewAccount1") {
                        NewAccount1(navController = navController)
                    }
                    composable("NewAccount2") {
                        NewAccount2(navController = navController)
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