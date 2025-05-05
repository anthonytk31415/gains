package com.example.gains.home.exercise

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.gains.R

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun ExerciseDetailsScreen(navController: NavController, exercise: Exercise) {
    var playVideo by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Dynamically resolve drawable resource from imageResName
    val imageResId = remember(exercise.imageResName) {
        context.resources.getIdentifier(exercise.imageResName, "drawable", context.packageName)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(text = exercise.name, style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(8.dp))

        if (playVideo) {
            AndroidView(
                factory = { ctx ->
                    WebView(ctx).apply {
                        settings.javaScriptEnabled = true
                        webViewClient = WebViewClient()
                        loadUrl(exercise.videoUrl)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .clickable { playVideo = true }
            ) {
                Image(
                    painter = painterResource(id = imageResId),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    tint = Color.White,
                    modifier = Modifier
                        .size(64.dp)
                        .align(Alignment.Center)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("INSTRUCTIONS", style = MaterialTheme.typography.titleMedium, color = Color.Blue)
        Text(text = exercise.instructions, style = MaterialTheme.typography.bodyLarge)

        Spacer(modifier = Modifier.height(16.dp))

        Text("FOCUS AREA", style = MaterialTheme.typography.titleMedium, color = Color.Blue)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            exercise.focusAreas.forEach {
                AssistChip(onClick = {}, label = { Text(it) })
            }
        }


        Spacer(modifier = Modifier.height(16.dp))

        Text("COMMON MISTAKES", style = MaterialTheme.typography.titleMedium, color = Color.Blue)
        Text(text = exercise.commonMistakes, style = MaterialTheme.typography.bodyLarge)

        Spacer(modifier = Modifier.height(16.dp))

        Text("BREATHING TIPS", style = MaterialTheme.typography.titleMedium, color = Color.Blue)
        Text(text = exercise.breathingTips, style = MaterialTheme.typography.bodyLarge)

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = { navController.popBackStack() }) {
                Text("CLOSE")
            }
        }
    }
}