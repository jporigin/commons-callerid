package com.origin.commons.callerid.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.origin.commons.callerid.di.AppProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OgCallerIdBootReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val coroutineScope = CoroutineScope(Dispatchers.Main)
            coroutineScope.launch {
                AppProvider(context).reminderRepository.scheduleAllReminderNotifications()
            }
        }
    }
}