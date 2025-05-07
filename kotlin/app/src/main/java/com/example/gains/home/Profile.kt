package com.example.gains.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.runBlocking

@Composable
fun ProfileScreen(navController: NavController) {
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top, // align from top
        horizontalAlignment = Alignment.Start // align all content to start (left)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            Text(text = "Name: ", modifier = Modifier.padding(end = 8.dp))
            Text(text = "John Doe")
        }

        // Email Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            Text(text = "Email: ", modifier = Modifier.padding(end = 8.dp))
            Text(text = "john@example.com")
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text(
                text = "Enter Age:",
                modifier = Modifier.weight(1f)
            )
            TextField(
                value = age,
                onValueChange = { age = it },
                modifier = Modifier.weight(2f),
                singleLine = true
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text(
                text = "Enter Height:",
                modifier = Modifier.weight(1f)
            )
            TextField(
                value = height,
                onValueChange = { height = it },
                modifier = Modifier.weight(2f),
                singleLine = true
            )
        }


        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text(
                text = "Enter Weight:",
                modifier = Modifier.weight(1f)
            )
            TextField(
                value = weight,
                onValueChange = { weight = it },
                modifier = Modifier.weight(2f),
                singleLine = true
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = {
                errorMessage = null

                // Input validations
                if (age.toIntOrNull() == null || height.toFloatOrNull() == null || weight.toFloatOrNull() == null) {
                    errorMessage = "Please enter valid age, height, and weight"
                    return@Button
                }

                // Prepare data
                val data = mapOf(
                    "age" to age,
                    "height" to height,
                    "weight" to weight,
                )

                println("$data");

                runBlocking {
                    try {
//                        val response = WorkoutService.sendWorkoutData()
//                        println("Response: $response")
                        navController.navigate("generatedWorkout")
                    } catch (e: Exception) {
                        errorMessage = "Failed to send data: ${e.message}"
                    }
                }

            }) {
                Text("Update")
            }
            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = Color.Red,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
        }
    }
}
