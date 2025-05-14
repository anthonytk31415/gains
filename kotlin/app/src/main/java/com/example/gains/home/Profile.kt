package com.example.gains.home

import android.util.Log
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gains.UserSession
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.runBlocking
import com.example.gains.home.network.UserService
import com.example.gains.home.model.UserProfile

@Composable
fun ProfileScreen(navController: NavController) {
    val userId = UserSession.userId

    var dob by remember { mutableStateOf("") }
    var height by remember { mutableFloatStateOf(0f) }
    var weight by remember { mutableFloatStateOf(0f) }
    var heightInput by remember { mutableStateOf("") }
    var weightInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isDataLoaded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Fetch user data once on load
    if (!isDataLoaded) {
        LaunchedEffect(Unit) {
            try {
                val user = userId?.let { UserService.getUserDetails(it) }
                Log.d("ProfileScreen3", "User: $user")

                if (user != null) {
                    dob = user.dob ?: ""
                    height = user.height ?: 0f
                    weight = user.weight ?: 0f
                    heightInput = user.height.toString()
                    weightInput = user.weight.toString()
                } else {
                    Log.w("ProfileScreen3", "User data is null for userId: $userId")
                }

                isDataLoaded = true
            } catch (e: Exception) {
                Log.e("ProfileScreen3", "Exception while loading user: ${e.message}")
                // Optional: Only set error if you care to show it
                // errorMessage = "Failed to load user: ${e.message}"
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        // Name Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            Text(text = "Name: ", modifier = Modifier.padding(end = 8.dp))
            UserSession.username?.let { Text(text = it) }
        }

        // Email Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            Text(text = "Email: ", modifier = Modifier.padding(end = 8.dp))
            UserSession.email?.let { Text(text = it) }
        }

        // Age input
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text(text = "Enter Date of Birth:", modifier = Modifier.weight(1f))
            TextField(
                value = dob,
                onValueChange = { dob = it },
                modifier = Modifier.weight(2f),
                singleLine = true
            )
        }

        // Height input
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text(text = "Enter Height:", modifier = Modifier.weight(1f))
            TextField(
                value = heightInput,
                onValueChange = { heightInput = it; height = it.toFloatOrNull() ?: 0f },
                modifier = Modifier.weight(2f),
                singleLine = true
            )
        }

        // Weight input
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text(text = "Enter Weight:", modifier = Modifier.weight(1f))
            TextField(
                value = weightInput,
                onValueChange = { weightInput = it; weight = it.toFloatOrNull() ?: 0f },
                modifier = Modifier.weight(2f),
                singleLine = true
            )
        }

        // Submit button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = {
                errorMessage = null

                val profile = userId?.let {
                    UserSession.email?.let { it1 ->
                        UserProfile(
                            user_id = userId,
                            dob = dob,
                            height = height,
                            weight = weight
                        )
                    }
                }

                runBlocking {
                    try {
                        Log.d("ProfileScreen2", "User ID: $userId")
                        Log.d("ProfileScreen", "Profile: $profile")
                        val response = userId?.let {
                            if (profile != null) {
                                Log.d("ProfileScreen", "$profile")
                                UserService.sendUserData(it, userProfile = profile)
                            }
                        }
                        Log.d("ProfileScreen", "Response: $response")
                        Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                        //navController.navigate("generatedWorkout")
                        //Add Toast saying User Profile updated successfully
                    } catch (e: Exception) {
                        errorMessage = "Failed to update data: ${e.message}"
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