package com.example.gains.onboarding

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import androidx.compose.foundation.clickable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.example.gains.UserSession
import com.example.gains.onboarding.FirebaseAuthSingleton.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.launch

@Composable
fun Login(navController: NavController, modifier: Modifier = Modifier) {

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Check if the user is already signed in
    //val currentUser = auth.currentUser
    //if (currentUser != null) {
        // If the user is signed in, navigate to HomeScreen
    //    navController.navigate("HomeScreen") {
    //        popUpTo("Login") { inclusive = true }
    //        launchSingleTop = true
    //    }
   // }

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
                //.requiredHeight(height = 513.dp)
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
                    text = "Login",
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    lineHeight = 9.38.em,
                    style = TextStyle(
                        fontSize = 16.sp
                    )
                )
                Text(
                    text = "Enter your username and password to login",
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
                //.requiredHeight(height = 184.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp, Alignment.Top),
                    modifier = Modifier
                        .requiredWidth(width = 327.dp)
                ) {
                    Text(
                        text = "Username or Email",
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        lineHeight = 10.71.em,
                        style = TextStyle(
                            fontSize = 14.sp
                        ),
                        modifier = Modifier
                            .requiredWidth(width = 129.dp)
                            .requiredHeight(height = 21.dp)
                    )
                    TextField(
                        value = username, // bind the value to the username state
                        onValueChange = {
                            username = it
                        }, // update the username/email state when the text changes
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
                                    //if (isLoginFormValid(username, password)) {
                                    //    navController.navigate("HomeNav")
                                    //} else {
                                    // You can show an error state or Toast here
                                //}
                            }
                        )
                    )
                    Text(
                        text = "Reset Password",
                        color = Color.White,
                        textDecoration = TextDecoration.Underline,
                        textAlign = TextAlign.End,
                        lineHeight = 10.71.em,
                        style = TextStyle(
                            fontSize = 14.sp,
                            shadow = Shadow(
                                color = Color.Black.copy(alpha = 0.25f),
                                offset = Offset(0f, 4f),
                                blurRadius = 4f
                            )
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .requiredHeight(height = 21.dp)
                            .clickable {
                                navController.navigate("ResetPassword")
                            }
                    )
                }
                Button(
                    onClick = {
                        if (isLoginFormValid(username, password)) {
                            if(isEmailValid(username)) {
                                auth.signInWithEmailAndPassword(username, password)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            UserSession.loadUserData(
                                                onComplete = {
                                                    val isNewAccount = false
                                                    navController.navigate("HomeNav/$isNewAccount") {
                                                        popUpTo("Login") { inclusive = true }
                                                        launchSingleTop = true
                                                    }
                                                },
                                                onError = { error ->
                                                    Log.e("Login", "Failed to load user data: ${error.message}")
                                                    Toast.makeText(context, "Failed to load user data", Toast.LENGTH_SHORT).show()
                                                }
                                            )
                                        } else {
                                            val errorMessage =
                                                task.exception?.message ?: "Login failed"
                                            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG)
                                                .show()
                                            // You can show a Snackbar or Toast here
                                        }
                                    }
                            } else {
                                coroutineScope.launch {
                                    val email = getEmailFromUsername(username)
                                    if (email != null) {
                                        auth.signInWithEmailAndPassword(email, password)
                                            .addOnCompleteListener { task ->
                                                if (task.isSuccessful) {
                                                    navController.navigate("HomeNav") {
                                                        popUpTo("Login") { inclusive = true }
                                                        launchSingleTop = true
                                                    }
                                                } else {
                                                    val errorMessage =
                                                        task.exception?.message ?: "Login failed"
                                                    Toast.makeText(
                                                        context,
                                                        errorMessage,
                                                        Toast.LENGTH_LONG
                                                    ).show()
                                                }
                                            }
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Username not found",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        } else {
                            Toast.makeText(context, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = "Continue",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(
                        8.dp,
                        Alignment.CenterHorizontally
                    ),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .requiredWidth(width = 327.dp)
                ) {
                    HorizontalDivider(
                        color = Color(0xffe6e6e6),
                        modifier = Modifier
                            .requiredHeight(height = 1.dp)
                            .weight(weight = 0.5f)
                    )
                    Text(
                        text = "or",
                        color = Color(0xffbdbdbd),
                        textAlign = TextAlign.Center,
                        lineHeight = 10.em,
                        style = TextStyle(
                            fontSize = 14.sp
                        )
                    )
                    HorizontalDivider(
                        color = Color(0xffe6e6e6),
                        modifier = Modifier
                            .requiredHeight(height = 1.dp)
                            .weight(weight = 0.5f)
                    )
                }
                Button(
                    onClick = {
                        navController.navigate("NewAccount1")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = "Create New Account",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

fun isLoginFormValid(username: String, password: String): Boolean {
    return username.isNotBlank() &&
            password.isNotBlank()
}

fun isEmailValid(email: String): Boolean {
    val emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$".toRegex()
    return email.matches(emailRegex)
}

suspend fun getEmailFromUsername(username: String): String? {
    // Initialize Firestore
    val firestore = FirebaseFirestore.getInstance()

    // Assuming you have a collection "users" where each document has fields like "username" and "email"
    val usersCollection = firestore.collection("users")

    // Query to find the user by their username
    val query = usersCollection.whereEqualTo("username", username)

    return try {
        // Firestore query is asynchronous, so use suspend function to handle it
        val result = query.get().await()
        Log.d("FirestoreQuery", "Query result size: ${result.size()}")
        // If the query returns a document, get the email
        if (result.isEmpty) {
            Log.d("FirestoreQuery", "No documents found for username: $username")
            null // Username not found
        } else {
            val userDocument = result.documents.first()
            val email = userDocument.getString("email") // Return the user's email
            Log.d("FirestoreQuery", "Retrieved email: $email")
            email
        }
    } catch (e: Exception) {
        // Handle any errors, such as no internet connection or Firestore issues
        Log.e("FirestoreQuery", "Error occurred: ${e.message}")
        null
    }
}