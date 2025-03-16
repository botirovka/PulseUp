package com.example.pulseup

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PulseUpApp : Application(){
    override fun onCreate() {
        super.onCreate()
        Log.d("mydebug", "onCreate: ")
        Log.d("mydebug", "${FirebaseApp.initializeApp(this)} ")

    }
}