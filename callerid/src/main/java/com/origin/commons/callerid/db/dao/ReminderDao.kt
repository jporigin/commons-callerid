package com.origin.commons.callerid.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.origin.commons.callerid.db.entity.ReminderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {

    @Upsert
    suspend fun insert(reminder: ReminderEntity)

    @Query("SELECT * FROM reminders order by id DESC")
    fun getAll(): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE id = :id")
    fun getReminderById(id: Int): Flow<ReminderEntity?>

    @Delete
    suspend fun delete(reminder: ReminderEntity)

    @Update
    suspend fun update(reminder: ReminderEntity)

}