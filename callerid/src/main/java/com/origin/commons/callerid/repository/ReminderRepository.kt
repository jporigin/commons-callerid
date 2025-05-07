package com.origin.commons.callerid.repository

import com.origin.commons.callerid.db.entity.ReminderEntity
import kotlinx.coroutines.flow.Flow

interface ReminderRepository {

    fun getReminder(): Flow<List<ReminderEntity>>

    suspend fun updateReminder(reminder: ReminderEntity)

    suspend fun insertReminder(reminder: ReminderEntity)

    suspend fun deleteReminder(reminder: ReminderEntity)

    suspend fun getReminderById(id: Int): Flow<ReminderEntity>

    suspend fun getReminderByIdNormal(id: Int): ReminderEntity

    suspend fun scheduleAllReminderNotifications()

}