package com.example.pulseup

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PulseUpApp : Application(){
    override fun onCreate() {
        super.onCreate()
        Log.d("mydebug", "onCreate: ")
        Log.d("mydebug", "${FirebaseApp.initializeApp(this)} ")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "fcm_channel",
                "FCM Channel",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel for Firebase notification"
            }

            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
        FirebaseMessaging.getInstance().subscribeToTopic("all")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("FCM", "Subscribe to all")
                }
                else {
                    Log.e("FCM", "Failed subscribe to all")
                }
            }
    }
}