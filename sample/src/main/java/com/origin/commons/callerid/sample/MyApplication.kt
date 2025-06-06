package com.origin.commons.callerid.sample

import com.origin.commons.callerid.CallerIdSDKApplication
import com.origin.commons.callerid.ads.AdFormat
import com.origin.commons.callerid.sample.helpers.Utils.nativeAds
import com.origin.commons.callerid.sample.ui.activity.MainActivity
import com.origin.commons.callerid.sample.ui.activity.SettingActivity
import com.origin.commons.callerid.sample.ui.activity.SplashActivity
import com.origin.commons.callerid.sample.ui.fragment.CIHomeScreenFragment

class MyApplication : CallerIdSDKApplication() {
    override fun onCreate() {
        super.onCreate()
        initCallerSDK()

    }

    // CallerID
    private fun initCallerSDK() {
        val isInitialized = initSDK()
        if (isInitialized) {
            initSDKAds(adFormat = AdFormat.NATIVE_BIG, adUnitId = nativeAds)
        }

        openSettings = { SettingActivity::class.java }
        openClass1 = { MainActivity::class.java }
        openClass2High = { SplashActivity::class.java }
        customFragmentProvider = { CIHomeScreenFragment() }

    }
}
