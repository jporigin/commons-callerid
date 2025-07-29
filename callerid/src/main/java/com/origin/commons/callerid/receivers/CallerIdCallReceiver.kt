package com.origin.commons.callerid.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import com.origin.commons.callerid.extensions.logE
import com.origin.commons.callerid.extensions.prefsHelper
import com.origin.commons.callerid.extensions.startLegacyForegroundService

class CallerIdCallReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        logE("CallerIdCallReceiver::onReceive:call")
        if (context == null || intent == null) {
            logE("onReceive: context: null or intent: null")
            return
        }
        val isMissedCallFeatureEnable = context.prefsHelper.isMissedCallFeatureEnable
        val isCompleteCallFeatureEnable = context.prefsHelper.isCompleteCallFeatureEnable
        val isNoAnswerFeatureEnable = context.prefsHelper.isNoAnswerFeatureEnable
        if (isMissedCallFeatureEnable || isCompleteCallFeatureEnable || isNoAnswerFeatureEnable) {
            if (intent.action == TelephonyManager.ACTION_PHONE_STATE_CHANGED) {
                context.startLegacyForegroundService()
            }
        }
    }
}