package com.copilot.soundnotifywearos

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

class NtfyNotificationListenerService : NotificationListenerService() {
    companion object {
        private const val TAG = "NtfyListenerService"
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val packageName = sbn.packageName
        
        // Controlla se la notifica arriva da ntfy
        if (packageName.contains("ntfy", ignoreCase = true)) {
            Log.d(TAG, "ntfy notification detected! Playing sound...")
            playLoudSound()
        }
    }

    private fun playLoudSound() {
        val prefs = getSharedPreferences("sound_prefs", Context.MODE_PRIVATE)
        val soundName = prefs.getString("selected_sound", "notify_sound") ?: "notify_sound"
        val volume = prefs.getFloat("volume_level", 1.0f)

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
