package com.example.gains.home

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gains.home.exercise.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.example.gains.R

@Composable
fun SettingsScreen(navController: NavController) {
    val context = LocalContext.current

    // Load exercises from JSON file once
    val exercises by remember {
        mutableStateOf(loadExercisesFromJson(context))
    }

    val groupedExercises = exercises
        .sortedBy { it.name }
        .groupBy { it.name.first().uppercaseChar() }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Exercises",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            groupedExercises.forEach { (letter, group) ->
                Column {
                    Text(
                        text = letter.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    group.forEach { exercise ->
                        val imageResId = context.resources.getIdentifier(
                            exercise.imageResName,
                            "drawable",
                            context.packageName
                        ).takeIf { it != 0 } ?: R.drawable.body_back

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    navController.navigate("exerciseDetail/${exercise.id}")
                                }
                                .padding(vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = exercise.name,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Image(
                                painter = painterResource(id = imageResId),
                                contentDescription = exercise.name,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp) // Adjust as needed
                            )
                        }
                    }

                }
            }
        }
    }
}