package com.origin.commons.callerid.states

import androidx.room.ColumnInfo
import com.origin.commons.callerid.db.entity.ReminderEntity


sealed interface ReminderState {
    data object Loading: ReminderState
    data class Success(val data: List<ReminderEntity?>): ReminderState
}