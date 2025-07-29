package com.origin.commons.callerid.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.work.impl.WorkDatabaseMigrations.MIGRATION_1_2
import com.origin.commons.callerid.db.dao.ReminderDao
import com.origin.commons.callerid.db.entity.ReminderEntity

@Database(entities = [ReminderEntity::class], version = 2, exportSchema = false)
abstract class CallEndDB : RoomDatabase() {

    abstract fun reminderDao(): ReminderDao

    companion object {
        private const val DATABASE_NAME = "call_end_demo.db"

        @Volatile
        private var INSTANCE: CallEndDB? = null
        fun getInstance(context: Context): CallEndDB {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(context.applicationContext, CallEndDB::class.java, DATABASE_NAME)
                    .addMigrations(MIGRATION_1_2) // 👈 Added migration
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE reminders ADD COLUMN message TEXT")
            }
        }
    }
}