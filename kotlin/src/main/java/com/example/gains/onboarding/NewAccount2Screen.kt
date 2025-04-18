package com.example.gains.onboarding

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.gains.onboarding.FirebaseAuthSingleton.auth

@Composable
fun NewAccount2(navController: NavController, email: String, modifier: Modifier = Modifier) {

    // Access the FirebaseAuth instance from the singleton
    //val auth = FirebaseAuthSingleton.auth

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    0f to Color(0xffe80707),
                    1f to Color(0xff0010f4),
                    start = Offset(0f, 0f),
                    end = Offset(1000f, 0f)
                )
            )
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.Top),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .padding(horizontal = 24.dp)
                .offset(y = (-100).dp)
        ) {
            Text(
                text = "gAIns",
                color = Color.White,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier
                    .requiredWidth(width = 210.dp)
                    .requiredHeight(height = 58.dp)
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp, Alignment.Top),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Create an account",
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    lineHeight = 9.38.em,
                    style = TextStyle(
                        fontSize = 16.sp
                    )
                )
                Text(
                    text = "Enter your username and password ",
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    lineHeight = 10.71.em,
                    style = TextStyle(
                        fontSize = 14.sp
                    )
                )
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Top),
                modifier = Modifier
                    .requiredWidth(width = 327.dp)
                //.requiredHeight(height = 249.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp, Alignment.Top),
                    modifier = Modifier
                        .requiredWidth(width = 327.dp)
                    //.requiredHeight(height = 193.dp)
                ) {
                    Text(
                        text = "Username",
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        lineHeight = 10.71.em,
                        style = TextStyle(
                            fontSize = 14.sp
                        ),
                        modifier = Modifier
                            .requiredWidth(width = 68.dp)
                            .requiredHeight(height = 21.dp)
                    )

                    TextField(
                        value = username, // bind the value to the username state
                        onValueChange = {
                            username = it
                        }, // update the email state when the text changes
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            disabledContainerColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = Color.Black
                        ),
                        textStyle = TextStyle(fontSize = 16.sp, color = Color.Black),
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(
                            onNext = {
                                // No need to do anything, focus moves automatically
                            }
                        )
                    )
                    Text(
                        text = "Password",
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        lineHeight = 10.71.em,
                        style = TextStyle(
                            fontSize = 14.sp
                        )
                    )
                    TextField(
                        value = password, // bind the value to the password state
                        onValueChange = {
                            password = it
                        }, // update the password state when the text changes
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            disabledContainerColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = Color.Black
                        ),
                        textStyle = TextStyle(fontSize = 16.sp, color = Color.Black),
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(
                            onNext = {
                                // No need to do anything, focus moves automatically
                            }
                        )
                    )
                    Text(
                        text = "Confirm Password",
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        lineHeight = 10.71.em,
                        style = TextStyle(
                            fontSize = 14.sp
                        )
                    )
                    TextField(
                        value = confirmPassword, // bind the value to the confirmPassword state
                        onValueChange = {
                            confirmPassword = it
                        },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            disabledContainerColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = Color.Black
                        ),
                        textStyle = TextStyle(fontSize = 16.sp, color = Color.Black),
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                if (isNewAccountFormValid(username, password, confirmPassword)) {
                                    createNewUser(email, password, username, context, navController) { isLoading = it }
                                } else {
                                    Toast.makeText(context, "Please fill out the form correctly", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    )
                }
                Button(
                    onClick = {
                        if (isNewAccountFormValid(username, password, confirmPassword)) {
                            createNewUser(email, password, username, context, navController) { isLoading = it }
                        } else {
                            Toast.makeText(context, "Please fill out the form correctly", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                    shape = RoundedCornerShape(8.dp),
                    enabled = !isLoading // Disable button when loading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White) // Show loading spinner
                    } else {
                        Text(
                            text = "Continue",
                            color = Color.White,
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
            }
            Text(
                textAlign = TextAlign.Center,
                lineHeight = 13.sp,
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            color = Color(0xffb4b4b4),
                            fontSize = 12.sp
                        )
                    ) { append("By clicking continue, you agree to our") }
                    withStyle(
                        style = SpanStyle(
                            color = Color(0xff828282),
                            fontSize = 12.sp
                        )
                    ) { append(" ") }
                    withStyle(
                        style = SpanStyle(
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    ) { append("Terms of Service") }
                    withStyle(
                        style = SpanStyle(
                            color = Color(0xff828282),
                            fontSize = 12.sp
                        )
                    ) { append(" ") }
                    withStyle(
                        style = SpanStyle(
                            color = Color(0xffb4b4b4),
                            fontSize = 12.sp
                        )
                    ) { append("and") }
                    withStyle(
                        style = SpanStyle(
                            color = Color(0xff828282),
                            fontSize = 12.sp
                        )
                    ) { append(" ") }
                    withStyle(
                        style = SpanStyle(
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    ) { append("Privacy Policy") }
                },
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }
}

fun isNewAccountFormValid(username: String, password: String, confirmPassword: String): Boolean {
    return username.isNotBlank() &&
            password.isNotBlank() &&
            confirmPassword.isNotBlank() &&
            password == confirmPassword
}

fun createNewUser(
    email: String,
    password: String,
    username: String,
    context: Context,
    navController: NavController,
    onLoadingChange: (Boolean) -> Unit
) {
    onLoadingChange(true) // Start loading

    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                val userId = user?.uid

                Log.d("CreateUser", "Account created successfully, userId: $userId")

                if (userId == null) {
                    Toast.makeText(context, "Error: User ID is null", Toast.LENGTH_SHORT).show()
                    onLoadingChange(false)
                    return@addOnCompleteListener
                }

                val db = FirebaseFirestore.getInstance()
                val userRef = db.collection("users").document(userId)

                userRef.set(mapOf(
                    "username" to username,
                    "email" to email
                    ))
                    .addOnSuccessListener {
                        Log.d("CreateUser", "Username added to Firestore successfully")
                        Toast.makeText(context, "Account created successfully!", Toast.LENGTH_SHORT).show()
                        onLoadingChange(false)
                        Log.d("CreateUser", "Navigating to HomeScreen")
                        Toast.makeText(context, "Navigating to Home", Toast.LENGTH_SHORT).show()
                        navController.navigate("HomeNav") {
                            popUpTo("Login") { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        onLoadingChange(false)
                    }
            } else {
                Toast.makeText(context, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                onLoadingChange(false)
            }
        }
}