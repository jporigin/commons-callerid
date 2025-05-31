package com.origin.commons.callerid.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.origin.commons.callerid.db.entity.ReminderEntity
import com.origin.commons.callerid.di.AppProvider
import com.origin.commons.callerid.repository.ReminderRepository
import com.origin.commons.callerid.states.ReminderState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class NotificationFragmentViewModel(context: Context) : ViewModel() {
    private val reminderRepository: ReminderRepository by lazy {
        AppProvider(context).reminderRepository
    }

    val reminderState = reminderRepository.getReminder()
        .map { ReminderState.Success(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = ReminderState.Loading
        )

    suspend fun saveReminder(reminder: ReminderEntity) {
        reminderRepository.insertReminder(reminder)
    }

    suspend fun deleteReminder(reminder: ReminderEntity) {
        reminderRepository.deleteReminder(reminder)
    }

    suspend fun updateReminder(reminder: ReminderEntity) {
        reminderRepository.updateReminder(reminder)
    }

}