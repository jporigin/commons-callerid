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
import androidx.core.app.NotificationCompat
import com.origin.commons.callerid.CallerIdSDKApplication
import com.origin.commons.callerid.R
import com.origin.commons.callerid.extensions.getOpenAppIntent
import com.origin.commons.callerid.extensions.logE
import com.origin.commons.callerid.helpers.CallerIdUtils.isNotificationPermissionGranted
import com.origin.commons.callerid.helpers.NotificationConfig
import com.origin.commons.callerid.model.NotificationInfo

class NotificationService(private val context: Context) {

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun showReminderNotification(notificationInfo: NotificationInfo) {
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
            Notification.Builder(context, REMINDER_CHANNEL_ID)
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

    fun notifyOverlayDeniedNotification() {
        if (!isNotificationPermissionGranted(context)) return

        createOverlayDeniedNotificationChannel()

        val callerIdSDKApplication = try {
            this.context.applicationContext as? CallerIdSDKApplication
        } catch (_: Exception) {
            null
        }

        val notificationConfig = callerIdSDKApplication?.notificationConfig ?: NotificationConfig()
        logE("notificationConfig: $notificationConfig")

        val smallIconDefault: Int = if (callerIdSDKApplication?.appLogoIcon != null) {
            callerIdSDKApplication.appLogoIcon ?: R.drawable.ci_notification
        } else {
            R.drawable.ci_notification
        }
        val smallIcon = notificationConfig.smallIcon ?: smallIconDefault

        val mClass1 = callerIdSDKApplication?.openClass1?.provide()
        val mClass2High = callerIdSDKApplication?.openClass2High?.provide()
        val pendingClass = when {
            notificationConfig.pendingClass != null -> notificationConfig.pendingClass
            mClass1 != null -> mClass1
            mClass2High != null -> mClass2High
            else -> null
        }

        logE("pendingClass: $pendingClass")
        if (pendingClass == null) return

        val notifyIntent = Intent(context, pendingClass).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            notifyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationCompat.Builder(context, OVERLAY_DENIED_CHANNEL_ID).setPriority(NotificationCompat.PRIORITY_HIGH)
        } else {
            @Suppress("DEPRECATION")
            NotificationCompat.Builder(context).setPriority(NotificationCompat.PRIORITY_HIGH)
        }
        builder.apply {
            setContentTitle(notificationConfig.title)
            setContentText(notificationConfig.description)
            setSmallIcon(smallIcon)
            pendingIntent?.let { setContentIntent(it) }
            setOngoing(true)
            setShowWhen(false)
            setSilent(true)
            setAutoCancel(true)
            setDefaults(NotificationCompat.DEFAULT_SOUND)
        }
        notificationManager.notify(notificationConfig.notificationId, builder.build().apply {
            flags = flags or NotificationCompat.FLAG_NO_CLEAR
        })
    }

    private fun createOverlayDeniedNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                OVERLAY_DENIED_CHANNEL_ID,
                OVERLAY_DENIED_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                setShowBadge(false)
                enableLights(true)
                setSound(null, null)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(REMINDER_CHANNEL_ID, REMINDER_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH).apply {
                enableVibration(true)
                setShowBadge(true)
                enableLights(true)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val REMINDER_CHANNEL_ID = "com.origin.commons.callerid"
        const val REMINDER_CHANNEL_NAME = "Caller Notification"

        const val OVERLAY_DENIED_CHANNEL_ID = "com.origin.commons.callerid.overlay_denied"
        const val OVERLAY_DENIED_CHANNEL_NAME = "Overlay Denied"
    }
}
