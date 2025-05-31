package com.origin.commons.callerid.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.origin.commons.callerid.db.dao.ReminderDao
import com.origin.commons.callerid.db.entity.ReminderEntity

@Database(entities = [ReminderEntity::class], version = 1, exportSchema = false)
abstract class CallEndDB : RoomDatabase() {

    abstract fun reminderDao(): ReminderDao

    companion object {
        const val DATABASE_NAME = "call_end_demo.db"

        @Volatile
        private var INSTANCE: CallEndDB? = null
        fun getInstance(context: Context): CallEndDB {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(context.applicationContext, CallEndDB::class.java, DATABASE_NAME).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }

}