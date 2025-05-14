package com.example.gains.home

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
<<<<<<< HEAD
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gains.home.model.WorkoutFormRequest
import com.example.gains.home.model.WorkoutViewModel
import com.example.gains.home.network.UserService
import com.example.gains.home.network.WorkoutService
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

=======
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.json.JSONArray
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
>>>>>>> b157bdb65763b647d090e004270fde623d8b275c

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
                    Button(onClick = { selection = "manual" }, modifier = Modifier.fillMaxWidth()) {
                        Text("Manual")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { selection = "ai" }, modifier = Modifier.fillMaxWidth()) {
                        Text("AI")
                    }
                }
            }
<<<<<<< HEAD

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
=======
            "manual" -> ManualWorkoutEntry(onBack = { selection = null })
            "ai" -> AIWorkoutForm(onBack = { selection = null }, navController = navController)
>>>>>>> b157bdb65763b647d090e004270fde623d8b275c
        }
    }
}

@Composable
fun ManualWorkoutEntry(onBack: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Manual Workout Entry Screen", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onBack) { Text("Back") }
    }
}

@Composable
fun InputRow(label: String, value: String, onValueChange: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "Enter $label:", modifier = Modifier.weight(1f))
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.weight(2f),
            singleLine = true
        )
    }
}

@Composable
fun GenderRow(selected: String?, onSelected: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text("Gender:", modifier = Modifier.weight(1f))
        Row(
            modifier = Modifier.weight(2f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            listOf("Male", "Female").forEach { gender ->
                RadioButton(
                    selected = selected == gender,
                    onClick = { onSelected(gender) }
                )
                Text(gender)
                Spacer(modifier = Modifier.width(16.dp))
            }
        }
    }
}

@Composable
fun LocationRow(selected: String?, onSelected: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text("Workout Location:", modifier = Modifier.weight(1f))
        Row(
            modifier = Modifier.weight(2f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            listOf("Gym", "Home").forEach { location ->
                RadioButton(
                    selected = selected == location,
                    onClick = { onSelected(location) }
                )
                Text(location)
                Spacer(modifier = Modifier.width(16.dp))
            }
        }
    }
}

@Composable
fun DropdownSelector(
    label: String,
    selected: String?,
    options: List<String>,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onSelect: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(label, modifier = Modifier.padding(end = 16.dp))
        Box(
            modifier = Modifier
                .weight(1f)
                .border(1.dp, Color.Gray, shape = RoundedCornerShape(4.dp))
                .clickable { onExpandedChange(true) }
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selected ?: "Select",
                    color = if (selected == null) Color.Gray else Color.Black
                )
                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { onExpandedChange(false) }) {
                options.forEach { option ->
                    DropdownMenuItem(text = { Text(option) }, onClick = {
                        onSelect(option)
                        onExpandedChange(false)
                    })
                }
            }
        }
    }
}

@Composable
fun WorkoutConfirmationScreen(
    workouts: List<Map<String, Any>>,
    onRetry: (done: () -> Unit) -> Unit,
    onSave: () -> Unit
) {
    var retrying by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Text("Confirm Your Workout", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))
        workouts.forEachIndexed { index, workout ->
            val exerciseSets = (workout["exercise_sets"] as? List<Map<String, Any>>) ?: emptyList()
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                tonalElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Day ${index + 1}", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    exerciseSets.forEach { set ->
                        Text("• Exercise ID ${set["exercise_id"]}: ${set["sets"]} sets x ${set["reps"]} reps")
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = {
                retrying = true
                onRetry { retrying = false }
            }) {
                if (retrying) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Retrying...")
                } else {
                    Text("Retry")
                }
            }
            Button(onClick = onSave) { Text("Save Workout") }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
<<<<<<< HEAD
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
=======
fun AIWorkoutForm(onBack: () -> Unit, navController: NavController) {
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf<String?>(null) }
    var workoutLocation by remember { mutableStateOf<String?>(null) }
    var experience by remember { mutableStateOf<String?>(null) }
    var expandedExperience by remember { mutableStateOf(false) }
    var expandedDay by remember { mutableStateOf(false) }
    val experienceOptions = listOf("Beginner", "Intermediate", "Advanced")
    val dayOptions = listOf("2", "3", "4", "5", "6")
    var days by remember { mutableStateOf<String?>(null) }
>>>>>>> b157bdb65763b647d090e004270fde623d8b275c
    val muscleOptions = listOf("Chest", "Back", "Shoulders", "Legs", "Biceps", "Triceps", "Abs")
    val selectedMuscles = remember { mutableStateMapOf<String, Boolean>() }
    var dob by remember { mutableStateOf("") }
    var height by remember { mutableFloatStateOf(0f) }
    var weight by remember { mutableFloatStateOf(0f) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
<<<<<<< HEAD
//    val workoutViewModel = viewModel<WorkoutViewModel>()

    val coroutineScope = rememberCoroutineScope()
    val userId = 1
    var isDataLoaded by remember { mutableStateOf(false) }

    muscleOptions.forEach { option ->
        if (!selectedMuscles.containsKey(option)) {
            selectedMuscles[option] = false
        }
=======
    var generatedWorkouts by remember { mutableStateOf<List<Map<String, Any>>?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }
    var formData by remember { mutableStateOf<Map<String, Any>?>(null) }

    muscleOptions.forEach { if (!selectedMuscles.containsKey(it)) selectedMuscles[it] = false }

    if (generatedWorkouts != null) {
        WorkoutConfirmationScreen(
            workouts = generatedWorkouts!!,
            onRetry = { done ->
                formData?.let { data ->
                    isSubmitting = true
                    val mockedUserId = "1"
                    sendGenerateWorkoutRequest(data, mockedUserId,
                        onResult = {
                            generatedWorkouts = it
                            isSubmitting = false
                            done()
                        },
                        onError = {
                            println("Error: $it")
                            isSubmitting = false
                            done()
                        }
                    )
                }
            },
            onSave = {
                println("workout saved")
                navController.navigate("home")
            }
        )
        return
>>>>>>> b157bdb65763b647d090e004270fde623d8b275c
    }
    goalOptions.forEach { option ->
        if (!goals.containsKey(option)) {
            goals[option] = false
        }
    }
    // Fetch user data once on load
    if (!isDataLoaded) {
        LaunchedEffect(Unit) {
            try {
                val user = UserService.getUserDetails(userId)
//                Log.d("2","$user")
                dob = user.dob
                height = user.height
                weight = user.weight
                isDataLoaded = true
            } catch (e: Exception) {
                errorMessage = "Failed to load user: ${e.message}"
            }
        }
    }

<<<<<<< HEAD
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
=======
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Text("AI Workout Form", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))
        InputRow("Age", age) { age = it }
        InputRow("Height", height) { height = it }
        InputRow("Weight", weight) { weight = it }
        GenderRow(gender) { gender = it }
        LocationRow(workoutLocation) { workoutLocation = it }
        DropdownSelector("Your Experience", experience, experienceOptions, expandedExperience, { expandedExperience = it }) { experience = it }
        DropdownSelector("Workout Days", days, dayOptions, expandedDay, { expandedDay = it }) { days = it }
        Text("Target Muscles", style = MaterialTheme.typography.bodyLarge)
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            muscleOptions.forEach { muscle ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = selectedMuscles[muscle] == true,
                        onCheckedChange = { selectedMuscles[muscle] = it }
>>>>>>> b157bdb65763b647d090e004270fde623d8b275c
                    )
                    Text(text = muscle, modifier = Modifier.padding(start = 4.dp))
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
            Button(onClick = onBack) { Text("Back") }
            Button(
                onClick = {
                    errorMessage = null
                    if (age.toIntOrNull() == null || height.toFloatOrNull() == null || weight.toFloatOrNull() == null) {
                        errorMessage = "Please enter valid age, height, and weight"
                        return@Button
                    }
                    if (workoutLocation == null || experience == null || gender == null || days == null || selectedMuscles.values.none { it }) {
                        errorMessage = "Please fill all the fields"
                        return@Button
                    }
                    val data = mapOf(
                        "age" to age,
                        "height" to height,
                        "weight" to weight,
                        "gender" to gender!!,
                        "location" to workoutLocation!!,
                        "experience" to experience!!,
                        "workout_days" to days!!,
                        "muscles" to selectedMuscles.filter { it.value }.keys.toList()
                    )
                    formData = data
                    isSubmitting = true
                    sendGenerateWorkoutRequest(
                        data,
                        FirebaseAuth.getInstance().currentUser?.uid ?: "anonymous",
                        onResult = {
                            generatedWorkouts = it
                            isSubmitting = false
                        },
                        onError = {
                            println("Error: $it")
                            isSubmitting = false
                        }
                    )
                }, enabled = !isSubmitting
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Submitting...")
                } else {
                    Text("Submit")
                }
            }
        }
<<<<<<< HEAD
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
                    goal = goals.filter { it.value }.keys.toList().toString(),
                    height = height,
                    weight = weight,
                    age = "12"
//                    dob = dob
                )


                coroutineScope.launch {
                    try {
                        Log.d("FormSubmit", "Calling WorkoutService.submitUserForm")
                        val response = WorkoutService.submitUserForm(userId, formData)
                        Log.d("FormSubmit", "response: $response")
                        workoutViewModel.setWorkout(response)
                        navController.navigate("generatedWorkout")
                    } catch (e: Exception) {
                        Log.e("FormSubmit", "Failed to send data: ${e.message}", e)
                    }
                }

            }) {
                Text("Submit")
            }
        }

=======
>>>>>>> b157bdb65763b647d090e004270fde623d8b275c
        if (errorMessage != null) {
            Text(text = errorMessage!!, color = Color.Red, modifier = Modifier.padding(top = 12.dp))
        }
    }
}

fun sendGenerateWorkoutRequest(
    data: Map<String, Any>,
    userId: String,
    onResult: (List<Map<String, Any>>) -> Unit,
    onError: (String) -> Unit
) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val url = URL("http://52.24.121.169:8000/api/1/workouts/generate/")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json")
            conn.doOutput = true

            val writer = OutputStreamWriter(conn.outputStream)
            writer.write(JSONObject(data).toString())
            writer.flush()
            writer.close()

            val response = conn.inputStream.bufferedReader().readText()
            val root = JSONObject(response)
            val jsonArray = root.getJSONArray("schedule")
            val parsed = mutableListOf<Map<String, Any>>()

            for (i in 0 until jsonArray.length()) {
                val dayObj = jsonArray.getJSONObject(i)
                val setsArray = dayObj.getJSONArray("exercise_sets")
                val sets = mutableListOf<Map<String, Any>>()
                for (j in 0 until setsArray.length()) {
                    val s = setsArray.getJSONObject(j)
                    sets.add(
                        mapOf(
                            "exercise_id" to s.getInt("exercise_id"),
                            "sets" to s.getInt("sets"),
                            "reps" to s.getInt("reps"),
                            "weight" to s.getDouble("weight")
                        )
                    )
                }
                parsed.add(mapOf("exercise_sets" to sets))
            }
            withContext(Dispatchers.Main) { onResult(parsed) }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) { onError(e.message ?: "Unknown error") }
        }
    }
}