package com.origin.commons.callerid.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import com.origin.commons.callerid.R
import com.origin.commons.callerid.extensions.prefsHelper
import com.origin.commons.callerid.model.NotificationInfo

class NotificationService(private val context: Context) {

    private lateinit var mNotification: Notification

    private val notificationManager = context
        .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun showNotification(notificationInfo: NotificationInfo) {
        createChannel()
        val bundle = Bundle().apply {
            putString("title", notificationInfo.title)
            putString("message", notificationInfo.description)
            putBoolean("notification", true)
        }

        var mClass2High: Class<*>? = try {
            Class.forName(context.prefsHelper.mClass2High)
        } catch (_: Exception) {
            null
        }

        val pendingIntent: PendingIntent? = try {
            if (mClass2High != null) {
                PendingIntent.getActivity(
                    context, notificationInfo.notificationId.hashCode(), Intent(context, mClass2High).putExtras(bundle).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK },
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notBuilder = Notification.Builder(context, CHANNEL_ID)
                .setAutoCancel(true)
                .setContentTitle(notificationInfo.title)
                .setStyle(Notification.BigTextStyle().bigText(notificationInfo.description))
                .setContentText(notificationInfo.description)
            try {
                if (pendingIntent != null) {
                    notBuilder.setContentIntent(pendingIntent)
                }
                notBuilder.setSmallIcon(R.drawable.ci_notification_logo)
            } catch (_: Exception) {
            }
            mNotification = notBuilder.build()
        } else {
            val notBuilder1 = Notification.Builder(context)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_MAX)
                .setContentTitle(notificationInfo.title)
                .setStyle(Notification.BigTextStyle().bigText(notificationInfo.description))
                .setSound(uri)
                .setContentText(notificationInfo.description)
            try {
                if (pendingIntent != null) {
                    notBuilder1.setContentIntent(pendingIntent)
                }
                notBuilder1.setSmallIcon(R.drawable.ci_notification_logo)
            } catch (_: Exception) {
            }
            mNotification = notBuilder1.build()
        }
        notificationManager.notify(notificationInfo.notificationId.hashCode(), mNotification)
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance)
            notificationChannel.enableVibration(true)
            notificationChannel.setShowBadge(true)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.parseColor("#e8334a")
            notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    companion object {
        const val CHANNEL_ID = "com.origin.commons.callerid"
        const val CHANNEL_NAME = "Sample Notification"
    }

}