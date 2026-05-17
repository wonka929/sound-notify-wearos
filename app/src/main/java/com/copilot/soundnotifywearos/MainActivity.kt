package com.copilot.soundnotifywearos

import android.content.ComponentName
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import android.content.pm.PackageManager
import androidx.core.app.NotificationManagerCompat
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            var isServiceEnabled by remember { mutableStateOf(false) }

            LaunchedEffect(Unit) {
                while (true) {
                    isServiceEnabled = isNotificationServiceEnabled()
                    delay(3000)
                }
            }

            WearApp(isServiceEnabled = isServiceEnabled)
        }
    }

    private fun isNotificationServiceEnabled(): Boolean {
        return NotificationManagerCompat.getEnabledListenerPackages(this).contains(packageName)
    }
}

@Composable
fun WearApp(isServiceEnabled: Boolean) {
    MaterialTheme {
        Scaffold(
            timeText = { TimeText() }
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        textAlign = TextAlign.Center,
                        text = "SoundNotify",
                        style = MaterialTheme.typography.title3
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        textAlign = TextAlign.Center,
                        text = if (isServiceEnabled) "SERVIZIO ATTIVO" else "SERVIZIO DISATTIVO",
                        style = MaterialTheme.typography.caption1,
                        color = if (isServiceEnabled) Color.Green else Color.Red
                    )
                    if (!isServiceEnabled) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            textAlign = TextAlign.Center,
                            text = "Usa ADB per abilitare l'accesso alle notifiche",
                            style = MaterialTheme.typography.caption2
                        )
                    }
                }
            }
        }
    }
}
