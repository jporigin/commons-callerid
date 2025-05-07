package com.origin.commons.callerid.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    @ColumnInfo("title") val title: String? = null,
    @ColumnInfo("date") val date: String? = null,
    @ColumnInfo("hours")val hours: String? = null,
    @ColumnInfo("minutes")val minutes: String? = null,
)