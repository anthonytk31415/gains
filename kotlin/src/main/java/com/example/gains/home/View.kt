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
import androidx.compose.foundation.clickable

@Composable
fun ViewScreen(navController: NavController) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "View Screen",
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = "Leg Raises",
                modifier = Modifier
                    .clickable {
                        navController.navigate("exerciseDetail/leg_raises")
                    }
                    .padding(8.dp)
            )

            Text(
                text = "Push Ups",
                modifier = Modifier
                    .clickable {
                        navController.navigate("exerciseDetail/push_ups")
                    }
                    .padding(8.dp)
            )
        }
    }
}
