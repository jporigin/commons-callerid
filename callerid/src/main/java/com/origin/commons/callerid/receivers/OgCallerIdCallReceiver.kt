package com.origin.commons.callerid.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import com.origin.commons.callerid.extensions.logE
import com.origin.commons.callerid.extensions.prefsHelper
import com.origin.commons.callerid.extensions.refreshCurrentAdsType
import com.origin.commons.callerid.extensions.startLegacyForegroundService
import com.origin.commons.callerid.helpers.CallerIdUtils.calculateDuration
import com.origin.commons.callerid.helpers.CallerIdUtils.formatTimeToString
import com.origin.commons.callerid.helpers.CallerIdUtils.getPhoneState
import com.origin.commons.callerid.helpers.CallerIdUtils.isNotificationPermissionGranted
import com.origin.commons.callerid.helpers.CallerIdUtils.isScreenOverlayEnabled
import com.origin.commons.callerid.services.NotificationService
import com.origin.commons.callerid.ui.activity.CallerIdActivity
import com.origin.commons.callerid.utils.callPhoneNumber
import java.util.Date

class OgCallerIdCallReceiver : BroadcastReceiver() {

    /**
     • 0. IDLE: No call activity.
     • 1. RINGING: Incoming call is ringing.
     • 2. OFFHOOK: A call is in progress (dialing, active, or on hold) and no other calls are ringing or waiting.
     */

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) {
            logE("OgCallerIdCallReceiver:onReceive:  context: null or intent: null")
            return
        }
        val prefs = context.prefsHelper
        if (prefs.isSkipCallerScreen) {
            return
        }
        val isMissedCallFeatureEnable = prefs.isMissedCallFeatureEnable
        val isCompleteCallFeatureEnable = prefs.isCompleteCallFeatureEnable
        val isNoAnswerFeatureEnable = prefs.isNoAnswerFeatureEnable

        if (isMissedCallFeatureEnable || isCompleteCallFeatureEnable || isNoAnswerFeatureEnable) {
            if (intent.action == TelephonyManager.ACTION_PHONE_STATE_CHANGED) {
                if (isScreenOverlayEnabled(context)) {
                    when(prefs.showOnlyCallerIdScreen) {
                        true -> {
                            prefs.callPhoneNumber = getPhoneNumber(intent)
                            val extraState = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
                            handleCallState(context, extraState)
                        }
                        false -> {
                            callPhoneNumber = getPhoneNumber(intent)
                            logE("OgCallerIdCallReceiver:onReceive: ${getPhoneState(intent)}")
                            context.startLegacyForegroundService()
                        }
                    }
                } else {
                    prefs.callPhoneNumber = getPhoneNumber(intent)
                    val extraState = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
                    handleCallState(context, extraState)
                }
            }
        }
    }

    private fun getPhoneNumber(intent: Intent): String {
        @Suppress("DEPRECATION")
        // Only the default dialer app can access the incoming number
        return intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER) ?: "Unknown"
    }

    private fun handleCallState(context: Context, state: String?) {
        logE(" OgCallerIdCallReceiver: onReceive:: $state ")
        val prefs = context.prefsHelper
        when (state) {
            TelephonyManager.EXTRA_STATE_RINGING -> {
                if (!prefs.isAnyIncomingCallWaiting) {
                    logE(" OgCallerIdCallReceiver: onReceive:: Incoming call ")
                    prefs.isAnyIncomingCallWaiting = true
                    prefs.isIncomingCallRinging = true
                    prefs.isOutgoingCallRinging = false
                    prefs.callStartTime = Date().time
                    prefs.callType = "Incoming call"
                }
            }

            TelephonyManager.EXTRA_STATE_OFFHOOK -> {
                if (!prefs.isAnyOutgoingCallWaiting) {
                    logE(" OgCallerIdCallReceiver: onReceive:: Outgoing call ")
                    prefs.isAnyOutgoingCallWaiting = true
                    prefs.isOutgoingCallRinging = true
                    prefs.isIncomingCallRinging = false
                    prefs.callStartTime = Date().time
                    prefs.callType = "Outgoing call"
                }
            }

            TelephonyManager.EXTRA_STATE_IDLE -> {
                logE(" OgCallerIdCallReceiver: onReceive:: IDLE_STATE")
                if (prefs.isAnyIncomingCallWaiting || prefs.isAnyOutgoingCallWaiting) {
                    logE(" OgCallerIdCallReceiver: onReceive:: inside isAnyIncomingCallWaiting = ${prefs.isAnyIncomingCallWaiting}, isAnyOutgoingCallWaiting = ${prefs.isAnyOutgoingCallWaiting}")
                    setUpCallerIDAct(context)
                }
            }
        }
    }

    private fun setUpCallerIDAct(context: Context) {
        val prefs = context.prefsHelper
        val isMissedCallFeatureEnable = prefs.isMissedCallFeatureEnable
        val isCompleteCallFeatureEnable = prefs.isCompleteCallFeatureEnable
        val isNoAnswerFeatureEnable = prefs.isNoAnswerFeatureEnable

        val shouldStartCallerId = if (prefs.isIncomingCallRinging) {
            isMissedCallFeatureEnable || isNoAnswerFeatureEnable
        } else if (prefs.isOutgoingCallRinging) {
            isCompleteCallFeatureEnable
        } else {
            false
        }
        if (shouldStartCallerId) {
            startCallerId(context)
        }
        prefs.isIncomingCallRinging = false
        prefs.isOutgoingCallRinging = false
        prefs.isAnyIncomingCallWaiting = false
        prefs.isAnyOutgoingCallWaiting = false
    }

    private fun startCallerId(context: Context) {
        val prefs = context.prefsHelper
        if (isScreenOverlayEnabled(context)) {
            context.refreshCurrentAdsType()
            val intent = Intent(context, CallerIdActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            intent.putExtra("phoneNumber", prefs.callPhoneNumber)
            intent.putExtra("time", formatTimeToString(prefs.callStartTime))
            intent.putExtra("duration", calculateDuration(prefs.callStartTime, Date().time))
            intent.putExtra("callType", prefs.callType)
            resetPrefs(context)
            context.startActivity(intent)
        } else if (isNotificationPermissionGranted(context) && prefs.notifyOverlayDenied) {
            val service = NotificationService(context)
            service.notifyOverlayDeniedNotification()
        }
    }

    private fun resetPrefs(context: Context) {
        context.prefsHelper.apply {
            callPhoneNumber = "Unknown"
            callStartTime = 0
            callType = ""
        }
    }

}