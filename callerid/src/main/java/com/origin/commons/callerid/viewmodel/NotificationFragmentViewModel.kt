package com.origin.commons.callerid.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.origin.commons.callerid.db.entity.ReminderEntity
import com.origin.commons.callerid.repository.ReminderRepository
import com.origin.commons.callerid.states.ReminderState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class NotificationFragmentViewModel @Inject constructor(
    private val reminderRepository: ReminderRepository
) : ViewModel() {

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