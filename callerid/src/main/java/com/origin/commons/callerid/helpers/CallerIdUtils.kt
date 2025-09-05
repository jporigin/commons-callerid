package com.origin.commons.callerid.helpers

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.Locale

object CallerIdUtils {

    fun isPhoneStatePermissionGranted(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
    }

    fun isNotificationPermissionGranted(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun isScreenOverlayEnabled(context: Context): Boolean {
        return try {
            Settings.canDrawOverlays(context)
        } catch (_: Exception) {
            false
        }
    }

    fun formatTimeToString(millis: Long): String {
        val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        return dateFormat.format(millis)
    }

    fun calculateDuration(startTime: Long, endTime: Long): String {
        val durationMillis = endTime - startTime

        // Calculate hours, minutes, and seconds
        val hours = durationMillis / (1000 * 60 * 60)
        val minutes = durationMillis % (1000 * 60 * 60) / (1000 * 60)
        val seconds = durationMillis % (1000 * 60) / 1000

        // Format the duration
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
    }

    fun getPhoneState(intent: Intent): Int {
        if (intent.extras != null && intent.extras!!.getString("state") != null) {
            val string = intent.extras!!.getString("state")
            if (TelephonyManager.EXTRA_STATE_IDLE == string) {
                return 0
            }
            if (TelephonyManager.EXTRA_STATE_OFFHOOK == string) {
                return 2
            }
            if (TelephonyManager.EXTRA_STATE_RINGING == string) {
                return 1
            }
        }
        return 0
    }

    fun isCIDPermissionAllowed(context: Context): Boolean {
        return isPhoneStatePermissionGranted(context) && isScreenOverlayEnabled(context)
    }

    fun isReadContactPermissionGranted(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED
    }

}