package com.origin.commons.callerid.sample

import com.origin.commons.callerid.CallerIdSDKApplication
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : CallerIdSDKApplication() {
    override fun onCreate() {
        super.onCreate()
    }
}
