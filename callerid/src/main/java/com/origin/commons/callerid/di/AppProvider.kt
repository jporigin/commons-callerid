package com.origin.commons.callerid.di

import android.app.AlarmManager
import android.content.Context
import com.origin.commons.callerid.db.CallEndDB
import com.origin.commons.callerid.db.dao.ReminderDao
import com.origin.commons.callerid.repository.ReminderRepository
import com.origin.commons.callerid.repository.ReminderRepositoryImpl

internal class AppProvider(private val mContext: Context) {
    private val appContext = mContext.applicationContext

    private val mDatabase: CallEndDB by lazy {
        CallEndDB.getInstance(appContext)
    }

    private val reminderDao: ReminderDao by lazy { mDatabase.reminderDao() }
    private val alarmManager: AlarmManager by lazy {
        mContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    val reminderRepository: ReminderRepository by lazy {
        ReminderRepositoryImpl(mContext, alarmManager, reminderDao)
    }
}
