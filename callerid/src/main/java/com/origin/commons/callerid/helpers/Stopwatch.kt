package com.origin.commons.callerid.helpers

import android.os.Looper
import java.util.concurrent.TimeUnit

class Stopwatch(private val onTick: (String) -> Unit) {

    private var startTimeMillis: Long = 0L
    private var elapsedTimeMillis: Long = 0L
    private var isRunning: Boolean = false

    private val handler = android.os.Handler(Looper.getMainLooper())
    private val runnable = object : Runnable {
        override fun run() {
            if (isRunning) {
                elapsedTimeMillis = System.currentTimeMillis() - startTimeMillis
                onTick(formatTime(elapsedTimeMillis))
                handler.postDelayed(this, 1000) // Update every second
            }
        }
    }

    fun start() {
        if (!isRunning) {
            startTimeMillis = System.currentTimeMillis() - elapsedTimeMillis // Resume from where it left off
            isRunning = true
            handler.post(runnable)
        }
    }

    fun pause() {
        if (isRunning) {
            isRunning = false
            handler.removeCallbacks(runnable)
        }
    }

    fun reset() {
        pause()
        elapsedTimeMillis = 0L
        onTick(formatTime(elapsedTimeMillis)) // Update UI to initial state
    }

    fun isRunning(): Boolean {
        return isRunning
    }

    private fun formatTime(millis: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(millis)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60

        return if (hours > 0) {
            String.format("%02d:%02d:%02d", hours, minutes, seconds) // HH:MM:SS
        } else {
            String.format("%02d:%02d", minutes, seconds) // MM:SS
        }
    }
}