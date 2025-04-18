package com.example.gains.home.exercise


import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import android.webkit.WebView
import androidx.compose.foundation.Image
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun ExerciseDetailsScreen(
    exerciseName: String,
    imageRes: Int? = null,
    videoUrl: String? = null,
    instructions: String,
    breathingTips: String,
    commonMistakes: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Top Visual: Video or Image
        if (!videoUrl.isNullOrEmpty()) {
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        settings.javaScriptEnabled = true
                        loadUrl(videoUrl)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            )
        } else if (imageRes != null) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Exercise Name
        Text(
            text = exerciseName,
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Instructions
        Text(
            text = "Instructions:",
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = instructions,
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Breathing Tips
        Text(
            text = "Breathing Tips:",
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = breathingTips,
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Common Mistakes
        Text(
            text = "Common Mistakes:",
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = commonMistakes,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}