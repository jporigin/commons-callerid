package com.origin.commons.callerid

import android.app.Application
import android.content.IntentFilter
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.MobileAds
import com.google.firebase.Firebase
import com.origin.commons.callerid.extensions.prefsHelper
import com.origin.commons.callerid.helpers.Utils
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.origin.commons.callerid.ads.AdFormat
import com.origin.commons.callerid.extensions.logEventE
import com.origin.commons.callerid.helpers.SharedPreferencesHelper
import com.origin.commons.callerid.receiver.OgCallerIdCallReceiver

open class CallerIdSDKApplication : Application() {

    var mSharedPreferencesHelper: SharedPreferencesHelper? = null

    var openSettingClass: (() -> Class<*>)? = null

    var openClass1: (() -> Class<*>)? = null
    var openClass2High: (() -> Class<*>)? = null

    var customHomeFragment: (() -> Fragment)? = null

    fun initSDK(): Boolean {
        try {
            Thread {
                MobileAds.initialize(this)
            }.start()
        } catch (_: Exception) {
        }
        registerCallReceiver()
        mFirebaseAnalytics = Firebase.analytics
        prefsHelper.apply {
            mSharedPreferencesHelper = this
            if (this.isMissedCallFeatureEnable || this.isCompleteCallFeatureEnable || this.isNoAnswerFeatureEnable) {
                if (!Utils.isPhoneStatePermissionGranted(applicationContext) && !Utils.isScreenOverlayEnabled(applicationContext)) {
                    return false
                }
            } else {
                return false
            }
        }
        logEventE("Initialized_OGCallerIdSDK")
        return true
    }


    private var mOgCallerIdCallReceiver: OgCallerIdCallReceiver? = null
    private fun registerCallReceiver() {
        try {
            if (mOgCallerIdCallReceiver == null) {
                mOgCallerIdCallReceiver = OgCallerIdCallReceiver()
                registerReceiver(mOgCallerIdCallReceiver, IntentFilter("android.intent.action.PHONE_STATE"))
            }
        } catch (_: Exception) {
        }
    }

    fun initSDKAds(adFormat: AdFormat, adUnitId: String, isShowAdsShimmerView: Boolean = true) {
        prefsHelper.apply {
            this.mAdFormat = adFormat.name
            this.mAdUnitId = adUnitId
            this.mIsShowAdsShimmerView = isShowAdsShimmerView
        }
    }

    /*
    *
    * */
    fun getMissedCallFeatureEnable(): Boolean? {
        return mSharedPreferencesHelper?.isMissedCallFeatureEnable
    }

    fun setMissedCallFeatureEnable(value: Boolean) {
        mSharedPreferencesHelper?.isMissedCallFeatureEnable = value
    }

    /*
    *
    * */
    fun getCompleteCallFeatureEnable(): Boolean? {
        return mSharedPreferencesHelper?.isCompleteCallFeatureEnable
    }

    fun setCompleteCallFeatureEnable(value: Boolean) {
        mSharedPreferencesHelper?.isCompleteCallFeatureEnable = value
    }

    /*
    *
    * */
    fun getNoAnswerFeatureEnable(): Boolean? {
        return mSharedPreferencesHelper?.isNoAnswerFeatureEnable
    }

    fun setNoAnswerFeatureEnable(value: Boolean) {
        mSharedPreferencesHelper?.isNoAnswerFeatureEnable = value
    }
    /*
    *
    * */


    companion object {
        @Volatile
        var mFirebaseAnalytics: FirebaseAnalytics? = null

        fun getFirebaseAnalytics(): FirebaseAnalytics {
            return mFirebaseAnalytics ?: synchronized(this) {
                mFirebaseAnalytics ?: Firebase.analytics.also { mFirebaseAnalytics = it }
            }
        }
    }
}