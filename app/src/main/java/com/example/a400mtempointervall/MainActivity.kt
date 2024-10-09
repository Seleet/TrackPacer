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
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast  // Importing Toast

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
    var timeRemaining by remember { mutableIntStateOf(0) } // Using mutableIntStateOf for optimization
    var isTimerRunning by remember { mutableStateOf(false) } // MutableState for dynamic changes
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
                //keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
            TextField(
                value = seconds,
                onValueChange = { seconds = it },
                label = { Text("Seconds") },
                //keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
            TextField(
                value = interval,
                onValueChange = { interval = it },
                label = { Text("Interval (meters)") },
                //keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            // Display remaining time until next interval
            Text(
                text = "Time Remaining: $timeRemaining seconds",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(16.dp)
            )

            Row {
                // Start Button
                Button(onClick = {
                    if (minutes.isNotEmpty() && seconds.isNotEmpty()) {
                        val totalSeconds = (minutes.toIntOrNull() ?: 0) * 60 + (seconds.toIntOrNull() ?: 0)
                        val intervalMeters = interval.toIntOrNull() ?: 50
                        isTimerRunning = true // Start the timer
                        scope.launch {
                            notifyUserAtInterval(
                                totalSeconds,
                                intervalMeters,
                                context,
                                timeRemainingSetter = { timeRemaining = it },
                                isTimerRunning = { isTimerRunning } // Pass the isTimerRunning state
                            )
                        }
                    }
                }) {
                    Text("Start")
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Stop Button
                Button(onClick = {
                    isTimerRunning = false // Stop the timer
                }) {
                    Text("Stop")
                }
            }
        }
    }
}

suspend fun notifyUserAtInterval(
    totalSecondsPerKm: Int,
    intervalMeters: Int,
    context: android.content.Context,
    timeRemainingSetter: (Int) -> Unit, // A function to update the UI with the remaining time
    isTimerRunning: () -> Boolean // A function to check the current value of isTimerRunning
) {
    val timePerMeter = totalSecondsPerKm / 1000.0
    val timePerInterval = timePerMeter * intervalMeters
    val mediaPlayer = MediaPlayer.create(context, R.raw.ringklocka)

    while (isTimerRunning()) {
        var countdownTime = timePerInterval.toInt()

        // Countdown loop for each interval
        while (countdownTime > 0 && isTimerRunning()) {
            timeRemainingSetter(countdownTime)
            delay(1000)
            countdownTime -= 1
        }

        // Stop the timer if it has been cancelled
        if (!isTimerRunning()) break

        // Play sound at the end of each interval
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
