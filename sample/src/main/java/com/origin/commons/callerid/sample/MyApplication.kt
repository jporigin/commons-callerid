package com.origin.commons.callerid.sample

import com.google.firebase.analytics.FirebaseAnalytics
import com.origin.commons.callerid.CallerIdSDKApplication
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : CallerIdSDKApplication() {

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate() {
        super.onCreate()

        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
    }
}
