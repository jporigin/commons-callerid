package com.origin.commons.callerid.receivers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.telephony.PhoneStateListener
import android.telephony.TelephonyCallback
import android.telephony.TelephonyManager
import androidx.core.content.ContextCompat
import com.origin.commons.callerid.CallerIdSDKApplication
import com.origin.commons.callerid.R
import com.origin.commons.callerid.extensions.logE
import com.origin.commons.callerid.extensions.prefsHelper
import com.origin.commons.callerid.helpers.Utils.calculateDuration
import com.origin.commons.callerid.helpers.Utils.formatTimeToString
import com.origin.commons.callerid.helpers.Utils.isScreenOverlayEnabled
import com.origin.commons.callerid.ui.activity.CallerIdActivity
import com.origin.commons.callerid.ui.wic.WICController
import java.util.Date
import com.origin.commons.callerid.utils.*


class CIdLegacyForegroundService : Service() {

    private var isIncomingCallRinging = false
    private var isAnyCallWaiting = false

    private var telephonyCallback: TelephonyCallback? = null // 12 and 12+

    @Suppress("DEPRECATION")
    private var phoneStateListener: PhoneStateListener? = null // 11 and below 11

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        setupForeground()
        return START_STICKY
    }

    /*
     • 0. IDLE: No call activity.
     • 1. RINGING: Incoming call is ringing.
     • 2. OFFHOOK: A call is in progress (dialing, active, or on hold) and no other calls are ringing or waiting.
     */

    override fun onCreate() {
        super.onCreate()
        setTheme(R.style.Caller_Main_Theme)
        val telephonyManager = getSystemService(TELEPHONY_SERVICE) as TelephonyManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val executor = ContextCompat.getMainExecutor(this)
            telephonyCallback = object : TelephonyCallback(), TelephonyCallback.CallStateListener {
                override fun onCallStateChanged(state: Int) {
                    logE(" CIdLegacyForegroundService:TelephonyCallback:>=31:")
                    handleCallState(state)
                }
            }
            telephonyManager.registerTelephonyCallback(executor, telephonyCallback as TelephonyCallback)
        } else {
            @Suppress("DEPRECATION")
            phoneStateListener = object : PhoneStateListener() {
                @Deprecated("Deprecated in Java")
                override fun onCallStateChanged(state: Int, phoneNumber: String?) {
                    logE(" CIdLegacyForegroundService:PhoneStateListener:<= 30:")
                    handleCallState(state)
                }
            }
            @Suppress("DEPRECATION")
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE)
        }
    }

    private fun handleCallState(state: Int) {
        logE(" CIdLegacyForegroundService: onReceive:: $state ")
        when (state) {
            TelephonyManager.CALL_STATE_RINGING -> {
                if (!isIncomingCallRinging) {
                    isIncomingCallRinging = true
                    isAnyCallWaiting = false
                    callStartTime = Date().time
                    callType = "Incoming call"
                    logE(" PopupOverlay::Show ")
                    // Show your popup overlay
                    WICController.showPopup(this@CIdLegacyForegroundService, 1)
                }
            }

            TelephonyManager.CALL_STATE_OFFHOOK -> {
                if (!isAnyCallWaiting) {
                    isAnyCallWaiting = true
                    isIncomingCallRinging = false
                    callStartTime = Date().time
                    callType = "Outgoing call"
                    logE(" PopupOverlay::Show ")
                    // Show your popup overlay
                    WICController.showPopup(this@CIdLegacyForegroundService, 2)
                }
            }


            TelephonyManager.CALL_STATE_IDLE -> {
                if (isIncomingCallRinging || isAnyCallWaiting) {
                    logE(" PopupOverlay::Hide ")
                    // Remove overlay
                    WICController.destroyPopup(this@CIdLegacyForegroundService) {
                        setUpCallerIDAct()
                    }
                }
            }
        }
    }


    private fun setUpCallerIDAct() {
        val mPref = this.prefsHelper
        val isMissedCallFeatureEnable = mPref.isMissedCallFeatureEnable
        val isCompleteCallFeatureEnable = mPref.isCompleteCallFeatureEnable
        val isNoAnswerFeatureEnable = mPref.isNoAnswerFeatureEnable

        val shouldStartCallerId = if (isIncomingCallRinging) {
            isMissedCallFeatureEnable || isNoAnswerFeatureEnable
        } else if (isAnyCallWaiting) {
            isCompleteCallFeatureEnable
        } else {
            false
        }
        if (shouldStartCallerId) {
            isIncomingCallRinging = false
            isAnyCallWaiting = false
            startCallerId()
        }
    }

    private fun startCallerId() {
        if (isScreenOverlayEnabled(this@CIdLegacyForegroundService)) {
            val intent = Intent(this@CIdLegacyForegroundService, CallerIdActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            intent.putExtra("phoneNumber", callPhoneNumber)
            intent.putExtra("time", formatTimeToString(callStartTime))
            intent.putExtra("duration", calculateDuration(callStartTime, Date().time))
            intent.putExtra("callType", callType)
            this@CIdLegacyForegroundService.startActivity(intent)
        }
    }


    override fun onDestroy() {
        logE("CIdLegacyForegroundService::onDestroy")
        val telephonyManager = getSystemService(TELEPHONY_SERVICE) as TelephonyManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && telephonyCallback != null) {
            telephonyManager.unregisterTelephonyCallback(telephonyCallback as TelephonyCallback)
        }
        super.onDestroy()
    }


    private val channelId = "CallerIdNotification"
    private val notificationId = 1001
    private fun setupForeground() {
        val contentTitle = "See call information"

        val callerIdSDKApplication = try {
            this.applicationContext as? CallerIdSDKApplication
        } catch (_: Exception) {
            null
        }
        val smallIcon: Int = if (callerIdSDKApplication?.appLogoIcon != null) {
            callerIdSDKApplication.appLogoIcon ?: R.drawable.ci_notification
        } else {
            R.drawable.ci_notification
        }
        val notificationBuilder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Caller ID", NotificationManager.IMPORTANCE_LOW).apply {
                description = "Caller ID Service Channel"
                enableLights(false)
                enableVibration(false)
                setShowBadge(false)
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            Notification.Builder(this, channelId)
        } else {
            Notification.Builder(this)
        }

        val notification = notificationBuilder.setContentTitle(contentTitle).setContentText("").setSmallIcon(smallIcon).build()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(notificationId, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_PHONE_CALL)
        } else {
            startForeground(notificationId, notification)
        }
    }

}