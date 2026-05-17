package com.copilot.soundnotifywearos

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import androidx.core.net.toUri

class NtfyNotificationListenerService : NotificationListenerService() {
    companion object {
        private const val TAG = "NtfyListenerService"
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service onCreate")
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        Log.d(TAG, "Notification Listener connected")
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        Log.d(TAG, "Notification Listener disconnected")
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val packageName = sbn.packageName
        Log.d(TAG, "Notification received from: $packageName")
        
        // Logga tutte le notifiche per debuggare
        if (packageName != "android" && packageName != "com.google.android.wearable.app") {
             Log.d(TAG, "Non-system notification: $packageName")
        }

        // Controlla se la notifica arriva da ntfy
        if (packageName.contains("ntfy", ignoreCase = true)) {
            Log.d(TAG, "ntfy notification detected (match contains)! Playing sound...")
            playLoudSound()
        }
    }

    private fun playLoudSound() {
        // Usa il suono personalizzato nelle risorse raw
        val mediaPlayer = MediaPlayer.create(this, R.raw.notify_sound) ?: return
        mediaPlayer.setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build(),
        )
        mediaPlayer.setOnCompletionListener { it.release() }
        mediaPlayer.start()
    }
}
