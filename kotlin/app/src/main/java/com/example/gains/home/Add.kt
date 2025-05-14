package com.example.gains.home

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gains.UserSession
import com.example.gains.home.model.WorkoutFormRequest
import com.example.gains.home.model.WorkoutViewModel
import com.example.gains.home.network.UserService
import com.example.gains.home.network.WorkoutService
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


@Composable
fun AddScreen(navController: NavController, workoutViewModel: WorkoutViewModel) {
    var selection by remember { mutableStateOf<String?>(null) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        when (selection) {
            null -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {


                    Button(
                        onClick = { selection = "manual" },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Manual")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { selection = "ai" },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("AI")
                    }
                }
            }

            "manual" -> {
                ManualWorkoutEntry(
                    onBack = { selection = null }
                )
            }

            "ai" -> {
                AIWorkoutForm(
                    navController = navController,
                    onBack = { selection = null },
                    workoutViewModel = workoutViewModel
                )
            }
        }
    }
}

@Composable
fun ManualWorkoutEntry(onBack: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Manual Workout Entry Screen", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onBack) {
            Text("Back")
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AIWorkoutForm(
    navController: NavController,
    onBack: () -> Unit,
    workoutViewModel: WorkoutViewModel
) {
    val scrollState = rememberScrollState()
    var expandedExperience by remember { mutableStateOf(false) }
    var expandedDay by remember { mutableStateOf(false) }
    val experienceOptions = listOf("Beginner", "Intermediate", "Advanced")
    var experience by remember { mutableStateOf<String?>(null) }
    var workoutLocation by remember { mutableStateOf<String?>(null) }
    var gender by remember { mutableStateOf<String?>(null) }
    var dayOptions = listOf("1","2","3","4","5","6","7")
    var days by remember { mutableStateOf<String?>(null) }
    val goalOptions = listOf("Muscle Gain", "Fat Loss", "Strength", "Toning", "Endurance")
    val goals = remember { mutableStateMapOf<String, Boolean>() }
    val muscleOptions = listOf("Chest", "Back", "Shoulders", "Legs", "Biceps", "Triceps", "Abs")
    val selectedMuscles = remember { mutableStateMapOf<String, Boolean>() }
//    var dob by remember { mutableStateOf("") }
//    var height by remember { mutableFloatStateOf(0f) }
//    var weight by remember { mutableFloatStateOf(0f) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
//    val workoutViewModel = viewModel<WorkoutViewModel>()

    val coroutineScope = rememberCoroutineScope()
    val userId = UserSession.userId
//    var isDataLoaded by remember { mutableStateOf(false) }

    muscleOptions.forEach { option ->
        if (!selectedMuscles.containsKey(option)) {
            selectedMuscles[option] = false
        }
    }
    goalOptions.forEach { option ->
        if (!goals.containsKey(option)) {
            goals[option] = false
        }
    }
    // Fetch user data once on load
//    if (!isDataLoaded) {
//        LaunchedEffect(Unit) {
//            try {
//                val user = userId?.let { UserService.getUserDetails(it) }
////                Log.d("2","$user")
//                if (user != null) {
//                    dob = user.dob
//                }
//                if (user != null) {
//                    height = user.height
//                }
//                if (user != null) {
//                    weight = user.weight
//                }
//                isDataLoaded = true
//            } catch (e: Exception) {
//                errorMessage = "Failed to load user: ${e.message}"
//            }
//        }
//    }

    Column(modifier = Modifier
        .fillMaxSize()
        .verticalScroll(scrollState)
        .padding(16.dp)) {
        Text("AI Workout Form Screen", style = MaterialTheme.typography.titleLarge)

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text(
                text = "Gender:",
                modifier = Modifier.weight(1f)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(2f)
            ) {
                RadioButton(
                    selected = gender == "Male",
                    onClick = { gender = "Male" }
                )
                Text(text = "Male")

                Spacer(modifier = Modifier.width(16.dp))


                RadioButton(
                    selected = gender == "Female",
                    onClick = { gender = "Female" }
                )
                Text(text = "Female")
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text(
                text = "Workout Location:",
                modifier = Modifier.weight(1f)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(2f)
            ) {
                RadioButton(
                    selected = workoutLocation == "Gym",
                    onClick = { workoutLocation = "Gym" }
                )
                Text(text = "Gym")

                Spacer(modifier = Modifier.width(16.dp))


                RadioButton(
                    selected = workoutLocation == "Home",
                    onClick = { workoutLocation = "Home" }
                )
                Text(text = "Home")
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text(
                text = "Your Experience",
                modifier = Modifier.padding(end = 16.dp)
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, Color.Gray, shape = RoundedCornerShape(4.dp))
                    .clickable { expandedExperience = true }
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = experience ?: "Select Experience",
                        color = if (experience == null) Color.Gray else Color.Black
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Dropdown"
                    )
                }

                DropdownMenu(
                    expanded = expandedExperience,
                    onDismissRequest = { expandedExperience = false }
                ) {
                    experienceOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                experience = option
                                expandedExperience = false
                            }
                        )
                    }
                }
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text(
                text = "No of days you want to workout",
                modifier = Modifier.padding(end = 16.dp)
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, Color.Gray, shape = RoundedCornerShape(4.dp))
                    .clickable { expandedDay = true }
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = days ?: "Select No of Days",
                        color = if (days == null) Color.Gray else Color.Black
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Dropdown"
                    )
                }

                DropdownMenu(
                    expanded = expandedDay,
                    onDismissRequest = { expandedDay = false }
                ) {
                    dayOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                days = option
                                expandedDay = false
                            }
                        )
                    }
                }
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text(
                text = "Target Muscles",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                muscleOptions.forEach { muscle ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = selectedMuscles[muscle] == true,
                            onCheckedChange = { isChecked ->
                                selectedMuscles[muscle] = isChecked
                            }
                        )
                        Text(
                            text = muscle,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text(
                text = "Your Goal",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                goalOptions.forEach { goal ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = goals[goal] == true,
                            onCheckedChange = { isChecked ->
                                goals[goal] = isChecked
                            }
                        )
                        Text(
                            text = goal,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = onBack) {
                Text("Back")
            }

            Button(onClick = {
                errorMessage = null

                // Input validations
                if (workoutLocation == null || experience == null || gender == null || days == null || selectedMuscles.values.none { it } || goals.values.none { it }) {
                    errorMessage = "Please fill all the fields"
                    return@Button
                }

                // Prepare data
                val formData = WorkoutFormRequest(
                    gender = gender!!,
                    location = workoutLocation!!,
                    experience = experience!!,
                    workout_days = days!!,
                    muscle_focus = selectedMuscles.filter { it.value }.keys.toList().toString(),
                    goal = goals.filter { it.value }.keys.toList().toString()
//                    height = height,
//                    weight = weight,
//                    age = "12"
//                    dob = dob
                )


                coroutineScope.launch {
                    try {
                        Log.d("FormSubmit", "Calling WorkoutService.submitUserForm")
                        val response = userId?.let { WorkoutService.submitUserForm(it, formData) }
                        Log.d("FormSubmit", "response: $response")
                        if (response != null) {
                            workoutViewModel.setWorkout(response)
                        }
                        navController.navigate("generatedWorkout")
                    } catch (e: Exception) {
                        Log.e("FormSubmit", "Failed to send data: ${e.message}", e)
                    }
                }

            }) {
                Text("Submit")
            }
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
