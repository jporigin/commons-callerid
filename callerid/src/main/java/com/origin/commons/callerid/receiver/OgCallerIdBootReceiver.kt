package com.origin.commons.callerid.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.origin.commons.callerid.repository.ReminderRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class OgCallerIdBootReceiver: BroadcastReceiver() {

    @Inject
    lateinit var reminderRepository: ReminderRepository

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val coroutineScope = CoroutineScope(Dispatchers.Main)
            coroutineScope.launch {
                reminderRepository.scheduleAllReminderNotifications()
            }
        }
    }
}