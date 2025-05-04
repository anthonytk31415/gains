package com.example.gains.onboarding

import android.content.Context
import android.util.Log
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.example.gains.onboarding.FirebaseAuthSingleton.auth

@Composable
fun NewAccount1(navController: NavController, modifier: Modifier = Modifier) {
    var email by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    //val auth = FirebaseAuth.getInstance()

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
                    text = "Enter your email to sign up for this app",
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
                modifier = Modifier.requiredWidth(width = 327.dp)
            ) {
                TextField(
                    value = email, // bind the value to the email state
                    onValueChange = { email = it }, // update the email state when the text changes
                    placeholder = {
                        Text(
                            text = "email@domain.com", // Placeholder text
                            color = Color(0xff828282),
                            style = TextStyle(fontSize = 16.sp)
                        )
                    },
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
                            if (email.isNotBlank()) {
                                // Navigate when the Enter key is pressed and email is not empty
                                //navController.navigate("NewAccount2")
                                checkEmailAndProceed(email, auth, context, navController){ isLoading = it }
                            }
                        }
                    )
                )
                Button(
                    onClick = {
                        //navController.navigate("NewAccount2")
                        if (email.isNotBlank()) {
                            // Navigate when the Enter key is pressed and email is not empty
                            //navController.navigate("NewAccount2")
                            checkEmailAndProceed(email, auth, context, navController){ isLoading = it }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    Text(
                        text = "Continue",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

fun checkEmailAndProceed(
    email: String,
    auth: FirebaseAuth,
    context: Context,
    navController: NavController,
    onLoadingChange: (Boolean) -> Unit
) {
    onLoadingChange(true)
    auth.fetchSignInMethodsForEmail(email)
        .addOnCompleteListener { task ->
            onLoadingChange(false)
            if (task.isSuccessful) {
                val signInMethods = task.result?.signInMethods ?: emptyList()
                if (signInMethods.isEmpty()) {
                    navController.navigate("NewAccount2/${email}")
                } else {
                    Toast.makeText(context, "Email is already registered.", Toast.LENGTH_SHORT).show()
                }
            } else {
                val exception = task.exception
                Log.e("FirebaseAuth", "Error checking email", exception)

                val message = when (exception) {
                    is FirebaseAuthInvalidCredentialsException -> "Invalid email format."
                    is FirebaseNetworkException -> "Network error. Check your internet connection."
                    else -> exception?.localizedMessage ?: "Unknown error occurred."
                }

                Toast.makeText(context, "Error: $message", Toast.LENGTH_LONG).show()
            }
        }
}