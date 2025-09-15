package com.origin.commons.callerid.sample

import com.ogmediaapps.callerid.CallerIdSDKApplication
import com.ogmediaapps.callerid.helpers.CallerIdSDK
import com.ogmediaapps.callerid.helpers.NotificationConfig
import com.ogmediaapps.callerid.model.ThemeConfig
import com.origin.commons.callerid.sample.helpers.Utils
import com.origin.commons.callerid.sample.ui.activity.MainActivity
import com.origin.commons.callerid.sample.ui.activity.PermissionActivity
import com.origin.commons.callerid.sample.ui.activity.SplashActivity
import com.origin.commons.callerid.sample.ui.fragment.CIHomeScreenFragment

class MyApplication : CallerIdSDKApplication() {

    override fun onCreate() {
        super.onCreate()
        initCallerSDK()
    }

    // initialize caller id sdk
    private fun initCallerSDK() {
        CallerIdSDK.init(this)
        setUpAdsUIDs()
        setUp(R.drawable.app_logo_, R.drawable.app_logo_icon)
        notifyOverlayDenied(true)
        setUpNotificationConfig(NotificationConfig(pendingClass = PermissionActivity::class.java))
        openClass1 = ActivityClassProvider { MainActivity::class.java }
        openClass2High = ActivityClassProvider { SplashActivity::class.java }
        customHomeFragment = FragmentClassProvider { CIHomeScreenFragment() }

    }

    override fun onCallerThemeChanged(themeConfig: ThemeConfig) {}

    private fun setUpAdsUIDs() {
        setUpAdsIDs(
            nativeBigIds = listOf(Utils.nativeBigId1, Utils.nativeBigId2, Utils.nativeBigId3),
            nativeSmallIds = listOf(Utils.nativeSmallId1, Utils.nativeSmallId2),
            bannerIds = listOf(Utils.bannerId1, Utils.bannerId2)
        )
    }
}