package com.origin.commons.callerid.helpers

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.Locale

object Utils {

    fun isPhoneStatePermissionGranted(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
        }
    }

    fun isNotificationPermissionGranted(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun isScreenOverlayEnabled(context: Context): Boolean {
        return Settings.canDrawOverlays(context)
    }

    fun formatTimeToString(millies: Long): String {
        val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        return dateFormat.format(millies)
    }

    fun calculateDuration(startTime: Long, endTime: Long): String {
        val durationMillis = endTime - startTime

        // Calculate hours, minutes, and seconds
        val hours = durationMillis / (1000 * 60 * 60)
        val minutes = durationMillis % (1000 * 60 * 60) / (1000 * 60)
        val seconds = durationMillis % (1000 * 60) / 1000

        // Format the duration
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

}