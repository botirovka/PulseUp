package com.example.pulseup.ui

import android.widget.Button
import android.os.Handler
import android.os.Looper

class LoadingAnimation(private val button: Button) {
    private var handler: Handler? = null
    private var dots = "."

    fun start() {
        handler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {
                dots = when (dots.length) {
                    1 -> ".."
                    2 -> "..."
                    else -> "."
                }
                button.text = dots
                handler?.postDelayed(this, 500) // Update every 500ms
            }
        }
        handler?.post(runnable)
    }

    fun stop() {
        handler?.removeCallbacksAndMessages(null)
        button.text = "..."
    }
}
