package com.origin.commons.callerid

import android.app.Application
import android.content.Context
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.MobileAds
import com.google.firebase.Firebase
import com.origin.commons.callerid.extensions.prefsHelper
import com.origin.commons.callerid.helpers.Utils
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.gson.Gson
import com.origin.commons.callerid.extensions.logEventE
import com.origin.commons.callerid.extensions.registerCallReceiver

open class CallerIdSDKApplication : Application() {

    fun setUpAdsIDs(nativeBigIds: List<String> = emptyList(), nativeSmallIds: List<String> = emptyList(), bannerIds: List<String> = emptyList()) {
        val newAdsRefreshType = buildString {
            if (nativeBigIds.isNotEmpty()) append("1")
            if (nativeSmallIds.isNotEmpty()) append("2")
            if (bannerIds.isNotEmpty()) append("3")
        }
        prefsHelper.apply {
            if (this.adsRefreshType != newAdsRefreshType) {
                this.adsRefreshIndex = -1
                this.adsRefreshType = newAdsRefreshType
            }
            if (nativeBigIds.isNotEmpty()) {
                this.nativeBigIDsList = Gson().toJson(nativeBigIds)
            }
            if (nativeSmallIds.isNotEmpty()) {
                this.nativeSmallIDsList = Gson().toJson(nativeSmallIds)
            }
            if (bannerIds.isNotEmpty()) {
                this.bannerIDsList = Gson().toJson(bannerIds)
            }
        }
    }
    fun setUpAdsIDs(adsRefreshType: String = "123", nativeBigIds: List<String> = emptyList(), nativeSmallIds: List<String> = emptyList(), bannerIds: List<String> = emptyList()) {
        prefsHelper.apply {
            if (this.adsRefreshType != adsRefreshType) {
                this.adsRefreshIndex = -1
                this.adsRefreshType = adsRefreshType
            }
            if (nativeBigIds.isNotEmpty()) {
                this.nativeBigIDsList = Gson().toJson(nativeBigIds)
            }
            if (nativeSmallIds.isNotEmpty()) {
                this.nativeSmallIDsList = Gson().toJson(nativeSmallIds)
            }
            if (bannerIds.isNotEmpty()) {
                this.bannerIDsList = Gson().toJson(bannerIds)
            }
        }
    }

    fun setUpPurchase(isPurchased: Boolean = false, skipCallerScreen: Boolean = false) {
        prefsHelper.apply {
            this.isPurchased = isPurchased
            this.isSkipCallerScreen = skipCallerScreen
        }
    }

    fun setUp(logo: Int, logoIcon: Int) {
        appLogo = logo
        appLogoIcon = logoIcon
    }

    var appLogo: Int? = null
    var appLogoIcon: Int? = null

    var openClass1: (() -> Class<*>)? = null
    var openClass2High: (() -> Class<*>)? = null

    var customHomeFragment: (() -> Fragment)? = null

    override fun onCreate() {
        super.onCreate()
        initSDK()
    }

    fun initSDK() {
        try {
            Thread {
                MobileAds.initialize(this)
            }.start()
        } catch (_: Exception) {
        }
        registerCallReceiver()
        mFirebaseAnalytics = Firebase.analytics
        logEventE("Initialized_OGCallerIdSDK")
        return
    }


    fun Context.isCIDPermissionAllowed(): Boolean {
        return Utils.isPhoneStatePermissionGranted(this) && Utils.isScreenOverlayEnabled(this)
    }

    fun Context.isCallerIDEnabled(): Boolean {
        if (!prefsHelper.isMissedCallFeatureEnable && !prefsHelper.isCompleteCallFeatureEnable && !prefsHelper.isNoAnswerFeatureEnable) {
            return false
        }
        return isCIDPermissionAllowed()
    }


    /*
    *
    * */
    fun getMissedCallFeatureEnable(): Boolean {
        return prefsHelper.isMissedCallFeatureEnable
    }

    fun setMissedCallFeatureEnable(value: Boolean) {
        prefsHelper.isMissedCallFeatureEnable = value
    }

    /*
    *
    * */
    fun getCompleteCallFeatureEnable(): Boolean {
        return prefsHelper.isCompleteCallFeatureEnable
    }

    fun setCompleteCallFeatureEnable(value: Boolean) {
        prefsHelper.isCompleteCallFeatureEnable = value
    }

    /*
    *
    * */
    fun getNoAnswerFeatureEnable(): Boolean {
        return prefsHelper.isNoAnswerFeatureEnable
    }

    fun setNoAnswerFeatureEnable(value: Boolean) {
        prefsHelper.isNoAnswerFeatureEnable = value
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