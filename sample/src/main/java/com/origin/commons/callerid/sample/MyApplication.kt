package com.origin.commons.callerid.sample

import com.origin.commons.callerid.CallerIdSDKApplication
import com.origin.commons.callerid.sample.helpers.Utils
import com.origin.commons.callerid.sample.ui.activity.MainActivity
import com.origin.commons.callerid.sample.ui.activity.SplashActivity
import com.origin.commons.callerid.sample.ui.fragment.CIHomeScreenFragment

class MyApplication : CallerIdSDKApplication() {
    override fun onCreate() {
        super.onCreate()
        initCallerSDK()
    }

    // initialize caller id sdk
    private fun initCallerSDK() {
        setUpAdsUIDs()
        setUp(R.drawable.app_logo_, R.drawable.app_logo_icon)
        openClass1 = { MainActivity::class.java }
        openClass2High = { SplashActivity::class.java }
        customHomeFragment = { CIHomeScreenFragment() }
    }

    private fun setUpAdsUIDs() {
        setUpAdsIDs(
            nativeBigIds = listOf(Utils.nativeBigId1, Utils.nativeBigId2, Utils.nativeBigId3),
            nativeSmallIds = listOf(Utils.nativeSmallId1, Utils.nativeSmallId2),
            bannerIds = listOf(Utils.bannerId1, Utils.bannerId2)
        )
    }
}