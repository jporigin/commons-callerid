package com.origin.commons.callerid.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import com.origin.commons.callerid.extensions.logE
import com.origin.commons.callerid.extensions.prefsHelper
import com.origin.commons.callerid.extensions.startLegacyForegroundService
import com.origin.commons.callerid.helpers.Utils.getPhoneState
import com.origin.commons.callerid.utils.callPhoneNumber


class OgCallerIdCallReceiver : BroadcastReceiver() {

    /*
     • 0. IDLE: No call activity.
     • 1. RINGING: Incoming call is ringing.
     • 2. OFFHOOK: A call is in progress (dialing, active, or on hold) and no other calls are ringing or waiting.
     */

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) {
            logE("OgCallerIdCallReceiver:onReceive:  context: null or intent: null")
            return
        }
        if (context.prefsHelper.isSkipCallerScreen) {
            return
        }
        val isMissedCallFeatureEnable = context.prefsHelper.isMissedCallFeatureEnable
        val isCompleteCallFeatureEnable = context.prefsHelper.isCompleteCallFeatureEnable
        val isNoAnswerFeatureEnable = context.prefsHelper.isNoAnswerFeatureEnable

        if (isMissedCallFeatureEnable || isCompleteCallFeatureEnable || isNoAnswerFeatureEnable) {
            if (intent.action == TelephonyManager.ACTION_PHONE_STATE_CHANGED) {
                logE("OgCallerIdCallReceiver:onReceive: ${getPhoneState(intent)}")
                callPhoneNumber = getPhoneNumber(intent)
                context.startLegacyForegroundService()
            }
        }
    }


    private fun getPhoneNumber(intent: Intent): String {
        @Suppress("DEPRECATION")
        // Only the default dialer app can access the incoming number
        return intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER) ?: "Unknown"
    }


    companion object {
        private const val TAG = "CallReceiver"
    }

}