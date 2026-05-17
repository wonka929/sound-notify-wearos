package com.copilot.soundnotifywearos

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationManagerCompat
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.material.*
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            var isServiceEnabled by remember { mutableStateOf(false) }
            val prefs = remember { getSharedPreferences("sound_prefs", Context.MODE_PRIVATE) }
            
            var selectedSound by remember { 
                mutableStateOf(prefs.getString("selected_sound", "notify_sound") ?: "notify_sound") 
            }
            var volume by remember { 
                mutableFloatStateOf(prefs.getFloat("volume_level", 1.0f)) 
            }

            LaunchedEffect(Unit) {
                while (true) {
                    isServiceEnabled = isNotificationServiceEnabled()
                    delay(3000)
                }
            }

            WearApp(
                isServiceEnabled = isServiceEnabled,
                selectedSound = selectedSound,
                volume = volume,
                onSoundSelected = { sound ->
                    selectedSound = sound
                    prefs.edit().putString("selected_sound", sound).apply()
                    playPreview(sound, volume)
                },
                onVolumeChanged = { newVolume ->
                    volume = newVolume
                    prefs.edit().putFloat("volume_level", newVolume).apply()
                }
            )
        }
    }

    private fun isNotificationServiceEnabled(): Boolean {
        return NotificationManagerCompat.getEnabledListenerPackages(this).contains(packageName)
    }

    private fun playPreview(soundName: String, volume: Float) {
        val resId = resources.getIdentifier(soundName, "raw", packageName)
        if (resId == 0) return
        
        val mediaPlayer = MediaPlayer.create(this, resId) ?: return
        mediaPlayer.setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build(),
        )
        mediaPlayer.setVolume(volume, volume)
        mediaPlayer.setOnCompletionListener { it.release() }
        mediaPlayer.start()
    }
}

@Composable
fun WearApp(
    isServiceEnabled: Boolean,
    selectedSound: String,
    volume: Float,
    onSoundSelected: (String) -> Unit,
    onVolumeChanged: (Float) -> Unit
) {
    val sounds = listOf("notify_sound", "notify_sound_2")
    
    MaterialTheme {
        Scaffold(
            timeText = { TimeText() }
        ) {
            ScalingLazyColumn(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Text(
                        text = "SoundNotify",
                        style = MaterialTheme.typography.title3
                    )
                }
                
                item {
                    Text(
                        text = if (isServiceEnabled) "SERVIZIO ATTIVO" else "SERVIZIO DISATTIVO",
                        style = MaterialTheme.typography.caption2,
                        color = if (isServiceEnabled) Color.Green else Color.Red
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Volume: ${(volume * 100).toInt()}%", style = MaterialTheme.typography.caption2)
                }

                item {
                    InlineSlider(
                        value = volume,
                        onValueChange = onVolumeChanged,
                        valueRange = 0f..1f,
                        steps = 9,
                        modifier = Modifier.padding(horizontal = 10.dp),
                        decreaseIcon = { Icon(Icons.Default.Clear, "Diminuisci") },
                        increaseIcon = { Icon(Icons.Default.Add, "Aumenta") }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Seleziona Suono:", style = MaterialTheme.typography.caption2)
                }

                items(sounds) { sound ->
                    val isSelected = sound == selectedSound
                    ToggleChip(
                        checked = isSelected,
                        onCheckedChange = { if (!isSelected) onSoundSelected(sound) },
                        label = { Text(sound) },
                        toggleControl = {
                            RadioButton(selected = isSelected)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
