package com.origin.commons.callerid.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.origin.commons.callerid.db.dao.ReminderDao
import com.origin.commons.callerid.db.entity.ReminderEntity

@Database(entities = [ReminderEntity::class], version = 1, exportSchema = false)
abstract class CallEndDB : RoomDatabase() {

    abstract fun reminderDao(): ReminderDao

    companion object {
        const val DATABASE_NAME = "call_end_demo.db"
    }

}