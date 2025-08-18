package com.origin.commons.callerid.helpers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build

class HomeKeyWatcher(private val context: Context) {
    interface OnHomeAndRecentsListener {
        fun onHomePressed()
        fun onRecentsPressed()
    }
    private val intentFilter = IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
    private var listener: OnHomeAndRecentsListener? = null
    private var lastRecentsPressTime: Long = 0
    private var receiver: HomeAndRecentsReceiver? = null
    private var isRegistered = false

    private inner class HomeAndRecentsReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action != Intent.ACTION_CLOSE_SYSTEM_DIALOGS) return
            val reason = intent.getStringExtra("reason") ?: return
            listener?.let {
                when (reason) {
                    "homekey" -> it.onHomePressed()
                    "recentapps" -> {
                        val currentTime = System.currentTimeMillis()
                        if (currentTime - lastRecentsPressTime > 1000) {
                            it.onRecentsPressed()
                            lastRecentsPressTime = currentTime
                        }
                    }
                }
            }
        }
    }

    fun setListener(listener: OnHomeAndRecentsListener) {
        this.listener = listener
        this.receiver = HomeAndRecentsReceiver()
    }

    fun startWatching() {
        if (!isRegistered && receiver != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.registerReceiver(receiver, intentFilter, Context.RECEIVER_NOT_EXPORTED)
            } else {
                context.registerReceiver(receiver, intentFilter)
            }
            isRegistered = true
        }
    }

    fun stopWatching() {
        if (isRegistered && receiver != null) {
            try {
                context.unregisterReceiver(receiver)
            } catch (_: Exception) {
            } finally {
                isRegistered = false
            }
        }
    }
}