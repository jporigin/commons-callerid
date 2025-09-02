package com.origin.commons.callerid.ui.wic.helpers

import android.app.NotificationManager
import android.content.Context
import android.media.AudioManager
import com.origin.commons.callerid.extensions.logE

object WicAudioManager {

    private var mAudioManager: AudioManager? = null

    var mIsRingtoneMuted: Boolean = false
    var mIsMicMuted: Boolean = false

    fun initAudioManager(context: Context) {
        if (mAudioManager == null) {
            mAudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            mIsRingtoneMuted = isRingtoneMuted(context)
            mIsMicMuted = isMicMuted(context)
        }
    }

    fun toggleMicState(context: Context) {
        mIsMicMuted = !mIsMicMuted
        muteMic(context, mIsMicMuted)
    }

    private fun muteMic(context: Context, mute: Boolean) {
        initAudioManager(context)
        logE("WicAudioManager", "Mic mute state: $mute")
        mAudioManager?.isMicrophoneMute = mute
    }

    fun isMicMuted(context: Context): Boolean {
        initAudioManager(context)
        return mAudioManager?.isMicrophoneMute == true
    }

    fun toggleRingtoneState(context: Context) {
        mIsRingtoneMuted = !mIsRingtoneMuted
        muteIncomingCallRingtone(context, mIsRingtoneMuted)
    }

    private fun muteIncomingCallRingtone(context: Context, mute: Boolean) {
        initAudioManager(context)

        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val hasDndAccess = nm.isNotificationPolicyAccessGranted

        try {
            if (hasDndAccess) {
                val muteState = if (mute) AudioManager.ADJUST_MUTE else AudioManager.ADJUST_UNMUTE
                mAudioManager?.adjustStreamVolume(AudioManager.STREAM_RING, muteState, 0)
                logE("WicAudioManager", "Ringtone mute state (DND): $mute")
            } else {
                if (mute) {
                    mAudioManager?.setStreamVolume(AudioManager.STREAM_RING, 0, 0)
                } else {
                    val maxVol = mAudioManager?.getStreamMaxVolume(AudioManager.STREAM_RING) ?: 7
                    val midVol = (maxVol * 0.5).toInt().coerceAtLeast(1)
                    mAudioManager?.setStreamVolume(AudioManager.STREAM_RING, midVol, 0)
                }
                logE("WicAudioManager", "Ringtone mute state (fallback): $mute")
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
            logE("WicAudioManager", "SecurityException: $e")
        } catch (e: Exception) {
            e.printStackTrace()
            logE("WicAudioManager", "Exception: $e")
        }
    }


    fun isRingtoneMuted(context: Context): Boolean {
        initAudioManager(context)
        // Safer method:
        return mAudioManager?.getStreamVolume(AudioManager.STREAM_RING) == 0
    }


}