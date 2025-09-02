package com.origin.commons.callerid.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.origin.commons.callerid.db.entity.ReminderEntity
import com.origin.commons.callerid.di.AppProvider
import com.origin.commons.callerid.model.toNotificationInfo
import com.origin.commons.callerid.services.NotificationService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class OgCallerIdReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return
        Log.e(TAG, "OgCallerIdReminderReceiver_called")
        val coroutineScope = CoroutineScope(Dispatchers.IO)
        val service = NotificationService(context)
        val reminderId = intent.getStringExtra("reminderId")
        coroutineScope.launch {
            val item: ReminderEntity? = try {
                reminderId?.let { AppProvider(context).reminderRepository.getReminderByIdNormal(it.toInt()) }
            } catch (_: Exception) {
                null
            }
            if (item != null) {
                val timeInMillis = provideTimeInMillis(item)
                val notificationInfo = timeInMillis.let { item.toNotificationInfo(it) }
                withContext(Dispatchers.Main){
                    notificationInfo.let { service.showReminderNotification(it) }
                }
            }
        }
    }

    private suspend fun provideTimeInMillis(reminder: ReminderEntity): Long = withContext(Dispatchers.Default) {
        val rightNow = Calendar.getInstance()
        val dateVal = reminder.date
        val hourVal = reminder.hours
        val minuteVal = reminder.minutes
        val datetimeToAlarm = Calendar.getInstance(Locale.getDefault())
        datetimeToAlarm.timeInMillis = System.currentTimeMillis()
        if (dateVal == "Today") {
            datetimeToAlarm.set(Calendar.DAY_OF_MONTH, rightNow.get(Calendar.DAY_OF_MONTH))
            datetimeToAlarm.set(Calendar.MONTH, rightNow.get(Calendar.MONTH))
        } else {
            val df = SimpleDateFormat("EEE, MMM dd",Locale.getDefault())
            val readDate = dateVal?.let { it1 -> df.parse(it1) }
            val cal = Calendar.getInstance()
            if (readDate != null) {
                cal.timeInMillis = readDate.time
            }
            datetimeToAlarm.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH))
            datetimeToAlarm.set(Calendar.MONTH, cal.get(Calendar.MONTH))
        }
        datetimeToAlarm.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hourVal!!))
        datetimeToAlarm.set(Calendar.MINUTE, Integer.parseInt(minuteVal!!))
        datetimeToAlarm.set(Calendar.SECOND, 0)
        datetimeToAlarm.set(Calendar.MILLISECOND, 0)

        val timeInMilliSeconds = datetimeToAlarm.timeInMillis
        timeInMilliSeconds
    }

    companion object {
        private const val TAG = "OgCallerIdReminderReceiver"
    }

}