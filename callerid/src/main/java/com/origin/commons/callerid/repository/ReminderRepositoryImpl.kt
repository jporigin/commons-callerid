package com.origin.commons.callerid.repository

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.origin.commons.callerid.db.dao.ReminderDao
import com.origin.commons.callerid.db.entity.ReminderEntity
import com.origin.commons.callerid.helpers.Utils.isNotificationPermissionGranted
import com.origin.commons.callerid.receivers.OgCallerIdReminderReceiver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ReminderRepositoryImpl(private val context: Context, private var alarmManager: AlarmManager, private val reminderDao: ReminderDao) : ReminderRepository {

    override fun getReminder(): Flow<List<ReminderEntity?>> {
        return reminderDao.getAll().flowOn(Dispatchers.IO).distinctUntilChanged()
    }

    override suspend fun updateReminder(reminder: ReminderEntity) {
        reminderDao.update(reminder)
    }

    override suspend fun insertReminder(reminder: ReminderEntity) {
        reminderDao.insert(reminder)
        setReminderNotification(reminder)
    }

    override suspend fun deleteReminder(reminder: ReminderEntity) {
        reminderDao.delete(reminder)
    }

    override suspend fun getReminderById(id: Int): Flow<ReminderEntity?> {
        return reminderDao.getReminderById(id)
    }

    override suspend fun getReminderByIdNormal(id: Int): ReminderEntity? {
        return reminderDao.getReminderById(id).firstOrNull()
    }

    private suspend fun setReminderNotification(reminder: ReminderEntity) = withContext(Dispatchers.Default) {
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
            val df = SimpleDateFormat("EEE, MMM dd", Locale.getDefault())
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
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeInMilliSeconds

        if (!isNotificationPermissionGranted(context)) {
            return@withContext
        }
        withContext(Dispatchers.Main) {
            val alarmIntent = Intent(context, OgCallerIdReminderReceiver::class.java)
            alarmIntent.putExtra("reminderId", reminder.id.toString())
            val pendingIntent = PendingIntent.getBroadcast(context, reminder.id!!.toInt(), alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        }
    }

    override suspend fun scheduleAllReminderNotifications() {
        val reminders = getReminder().firstOrNull()
        if (!reminders.isNullOrEmpty()) {
            reminders.forEach { reminder ->
                if (reminder != null) {
                    setReminderNotification(reminder)
                }
            }
        }
    }
}