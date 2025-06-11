package com.origin.commons.callerid.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.telephony.TelephonyManager
import android.util.Log
import com.origin.commons.callerid.extensions.logE
import com.origin.commons.callerid.extensions.prefsHelper
import com.origin.commons.callerid.helpers.Utils
import com.origin.commons.callerid.helpers.Utils.calculateDuration
import com.origin.commons.callerid.helpers.Utils.formatTimeToString
import com.origin.commons.callerid.ui.activity.OgCallerIdActivity
import java.util.Date

class OgCallerIdCallReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) {
            Log.e(TAG, "onReceive: context: null or intent: null")
            return
        }

        val isMissedCallFeatureEnable = context.prefsHelper.isMissedCallFeatureEnable
        val isCompleteCallFeatureEnable = context.prefsHelper.isCompleteCallFeatureEnable
        val isNoAnswerFeatureEnable = context.prefsHelper.isNoAnswerFeatureEnable

        if (intent.action == TelephonyManager.ACTION_PHONE_STATE_CHANGED) {
            val extraState = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
            val phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
            when (extraState) {
                TelephonyManager.EXTRA_STATE_RINGING -> {
                    if (!isRinging) {
                        isRinging = true
                        isOutgoingRinging = false
                        isIncomingRinging = true
                        handleIncomingCall(context, phoneNumber)
                    }
                }

                TelephonyManager.EXTRA_STATE_OFFHOOK -> {
                    if (!isRingingTwo) {
                        isRingingTwo = true
                        isIncomingRinging = false
                        isOutgoingRinging = true
                        handleAnsweredCall(context, phoneNumber)
                    }
                }

                TelephonyManager.EXTRA_STATE_IDLE -> {
                    if (isRinging || isRingingTwo) {
                        if (isIncomingRinging) {
                            Log.e(TAG, "onReceive: isIncomingRinging::: true")
                            if (isMissedCallFeatureEnable && isNoAnswerFeatureEnable && isCompleteCallFeatureEnable) {
                                Log.e(TAG, "onReceive: isIncomingRinging::: All Preference::: true")
                                handleEndedCall(context, phoneNumber, intent)
                            } else {
                                if (isMissedCallFeatureEnable) {
                                    Log.e(TAG, "onReceive: isIncomingRinging::: isMissedCallFeatureEnable::: true")
                                    handleEndedCall(context, phoneNumber, intent)
                                } else if (isNoAnswerFeatureEnable) {
                                    Log.e(TAG, "onReceive: isIncomingRinging::: isNoAnswerFeatureEnable::: true")
                                    handleEndedCall(context, phoneNumber, intent)
                                }
                            }
                        } else if (isOutgoingRinging) {
                            Log.e(TAG, "onReceive: isOutgoingRinging::: true")
                            if (isMissedCallFeatureEnable && isNoAnswerFeatureEnable && isCompleteCallFeatureEnable) {
                                Log.e(TAG, "onReceive: isOutgoingRinging::: All Preference::: true")
                                handleEndedCall(context, phoneNumber, intent)
                            } else {
                                if (isCompleteCallFeatureEnable) {
                                    Log.e(TAG, "onReceive: isOutgoingRinging::: isCompleteCallFeatureEnable::: true")
                                    handleEndedCall(context, phoneNumber, intent)
                                }
                            }
                        }
                        isRinging = false
                        isRingingTwo = false
                        isIncomingRinging = false
                        isOutgoingRinging = false
                    }
                }
            }
        }
    }

    private fun handleIncomingCall(context: Context, phoneNumber: String?) {
        time = Date().time
        callType = "Incoming call"
    }

    private fun handleAnsweredCall(context: Context, phoneNumber: String?) {
        time = Date().time
        callType = "Outgoing call"
    }

    private fun handleEndedCall(context: Context, phoneNumber: String?, intent: Intent) {
        try {
            if (Utils.isScreenOverlayEnabled(context)) {
                startDetailActivity(context, phoneNumber, time, callType)
            }
        } catch (_: Exception) {
        }
    }

    private fun startDetailActivity(context: Context, phoneNumber: String?, time: Long, callType: String) {
        val intent = Intent(context.applicationContext, OgCallerIdActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        intent.putExtra("phoneNumber", phoneNumber)
        intent.putExtra("time", formatTimeToString(time))
        intent.putExtra("duration", calculateDuration(time, Date().time))
        intent.putExtra("callType", callType)
        context.applicationContext.startActivity(intent)
    }

    companion object {
        private const val TAG = "CallReceiver"
        private var time: Long = 0
        private var callType = ""
        private var isRinging: Boolean = false
        private var isRingingTwo: Boolean = false
        private var isIncomingRinging: Boolean = false
        private var isOutgoingRinging: Boolean = false
    }

}