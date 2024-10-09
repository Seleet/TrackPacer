package com.example.a400mtempointervall

import android.os.Bundle
import android.media.MediaPlayer
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MyApp()
        }
    }
}

@Composable
fun MyApp() {
    var minutes by remember { mutableStateOf("") }
    var seconds by remember { mutableStateOf("") }
    var interval by remember { mutableStateOf("50") }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            TextField(
                value = minutes,
                onValueChange = { minutes = it },
                label = { Text("Minutes") },
               
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
            TextField(
                value = seconds,
                onValueChange = { seconds = it },
                label = { Text("Seconds") },

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
            TextField(
                value = interval,
                onValueChange = { interval = it },
                label = { Text("Interval (meters)") },

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
            Button(onClick = {
                if (minutes.isNotEmpty() && seconds.isNotEmpty()) {
                    val totalSeconds = (minutes.toIntOrNull() ?: 0) * 60 + (seconds.toIntOrNull() ?: 0)
                    val intervalMeters = interval.toIntOrNull() ?: 50
                    scope.launch {
                        notifyUserAtInterval(totalSeconds, intervalMeters, context)
                    }
                }
            }) {
                Text("Start")
            }
        }
    }
}

suspend fun notifyUserAtInterval(totalSecondsPerKm: Int, intervalMeters: Int, context: android.content.Context) {
    val timePerMeter = totalSecondsPerKm / 1000.0
    val timePerInterval = timePerMeter * intervalMeters
    val mediaPlayer = MediaPlayer.create(context, R.raw.ringklocka) // Replace with the actual file name in res/raw

    while (true) {
        delay((timePerInterval * 1000).toLong())

        // Play the custom m4a sound
        if (!mediaPlayer.isPlaying) {
            mediaPlayer.start()
        }

        // Show a Toast notification
        Toast.makeText(context, "Beep! You should have run $intervalMeters meters!", Toast.LENGTH_SHORT).show()
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyApp()
}
