package com.example.gains.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


@Composable
fun BaseScreen(
    username: String = "John Doe",        // The username shown in the top bar
    onLogout: () -> Unit = {},            // A lambda function to handle logout
    content: @Composable () -> Unit       // The unique screen content
) {
    Scaffold(
        topBar = {
            TopNavBar(username = username, onLogout = onLogout)
        },
        content = { innerPadding ->
            // Padding for content area (avoids overlap with top bar)
            Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
                content()   // Displays the child composable (like HomeScreen's content)
            }
        }
    )
}