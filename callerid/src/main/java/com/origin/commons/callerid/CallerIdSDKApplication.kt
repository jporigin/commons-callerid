package com.origin.commons.callerid

import android.app.Activity
import android.app.Application
import android.content.IntentFilter
import com.google.android.gms.ads.MobileAds
import com.google.firebase.Firebase
import com.origin.commons.callerid.extensions.prefsHelper
import com.origin.commons.callerid.extensions.showCustomToast
import com.origin.commons.callerid.helpers.Utils
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.origin.commons.callerid.ads.AdFormat
import com.origin.commons.callerid.extensions.logE
import com.origin.commons.callerid.extensions.logEventE
import com.origin.commons.callerid.helpers.SharedPreferencesHelper
import com.origin.commons.callerid.receiver.OgCallerIdCallReceiver

open class CallerIdSDKApplication : Application() {

    var mSharedPreferencesHelper: SharedPreferencesHelper? = null
    private var mActivity: Activity? = null
    fun initSDK(activity: Activity): Boolean {
        mActivity = activity
        try {
            MobileAds.initialize(this)
        } catch (_: Exception) {
        }

        registerCallReceiver(activity)
        mFirebaseAnalytics = Firebase.analytics
        activity.prefsHelper.apply {
            mSharedPreferencesHelper = this
            if (this.isMissedCallFeatureEnable || this.isCompleteCallFeatureEnable || this.isNoAnswerFeatureEnable) {
                if (!Utils.isPermissionAlreadyGranted(activity) && !Utils.isScreenOverlayEnabled(activity)) {
                    activity.showCustomToast(R.string.required_permission_desc)
                    return false
                }
            } else {
                return false
            }
        }
        activity.logEventE("Initialized_OGCallerIdSDK")
        return true
    }

    fun setUpClassToOpenApp(class1: Class<*>, class2High: Class<*>) {
        mActivity?.prefsHelper?.apply {
            this.mClass1 = class1.name
            this.mClass2High = class2High.name
        }
        logE("setUpClass::${class1.name}::${class2High.name}")
    }

    private var mOgCallerIdCallReceiver: OgCallerIdCallReceiver? = null
    private fun registerCallReceiver(activity: Activity) {
        try {
            if (!activity.isFinishing && mOgCallerIdCallReceiver == null) {
                mOgCallerIdCallReceiver = OgCallerIdCallReceiver()
                activity.registerReceiver(mOgCallerIdCallReceiver, IntentFilter("android.intent.action.PHONE_STATE"))
            }
        } catch (_: Exception) {
        }
    }

    fun initSDKAds(adFormat: AdFormat, adUnitId: String, isShowAdsShimmerView: Boolean = true) {
        mActivity?.prefsHelper?.apply {
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