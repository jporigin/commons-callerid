package com.origin.commons.callerid.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import com.origin.commons.callerid.CallerIdSDKApplication
import com.origin.commons.callerid.R
import com.origin.commons.callerid.extensions.getOpenAppIntent
import com.origin.commons.callerid.helpers.Utils.isNotificationPermissionGranted
import com.origin.commons.callerid.model.NotificationInfo

class NotificationService(private val context: Context) {

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun showNotification(notificationInfo: NotificationInfo) {
        if (!isNotificationPermissionGranted(context)) return

        createChannel()
        val callerIdSDKApplication = try {
            context.applicationContext as? CallerIdSDKApplication
        } catch (_: Exception) {
            null
        }
        val smallIcon: Int = if (callerIdSDKApplication?.appLogoIcon != null) {
            callerIdSDKApplication.appLogoIcon ?: R.drawable.ci_notification
        } else {
            R.drawable.ci_notification
        }


        val intent = context.getOpenAppIntent()?.apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            putExtras(Bundle().apply {
                putString("title", notificationInfo.title)
                putString("message", notificationInfo.description)
                putBoolean("notification", true)
            })
        }

        val pendingIntent = intent?.let {
            try {
                PendingIntent.getActivity(context, notificationInfo.notificationId.hashCode(), it, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            } catch (e: Exception) {
                null
            }
        }

        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(context, CHANNEL_ID)
        } else {
            @Suppress("DEPRECATION")
            Notification.Builder(context).setPriority(Notification.PRIORITY_MAX).setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
        }
        builder.apply {
            setAutoCancel(true)
            setContentTitle(notificationInfo.title)
            setContentText(notificationInfo.description)
            setStyle(Notification.BigTextStyle().bigText(notificationInfo.description))
            setSmallIcon(smallIcon)
            pendingIntent?.let { setContentIntent(it) }
        }
        notificationManager.notify(notificationInfo.notificationId.hashCode(), builder.build())
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH).apply {
                enableVibration(true)
                setShowBadge(true)
                enableLights(true)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID = "com.origin.commons.callerid"
        const val CHANNEL_NAME = "Caller Notification"
    }
}
