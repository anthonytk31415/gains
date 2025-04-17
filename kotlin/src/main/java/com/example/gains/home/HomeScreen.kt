package com.example.gains.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gains.onboarding.FirebaseAuthSingleton.auth

@Composable
fun HomeScreen(navController: NavController) {
    BaseScreen(
        username = "John Doe",
        onLogout = {
            auth.signOut()
            navController.navigate("Login") {
                popUpTo(0) { inclusive = true } // clear entire backstack
                launchSingleTop = true
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Welcome to Gains App",
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}
