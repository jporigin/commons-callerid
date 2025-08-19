package com.origin.commons.callerid.ui.activity

import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.origin.commons.callerid.CallerIdSDKApplication
import com.origin.commons.callerid.extensions.logE
import com.origin.commons.callerid.extensions.prefsHelper
import com.origin.commons.callerid.helpers.DARK_THEME
import com.origin.commons.callerid.helpers.LIGHT_THEME

abstract class CallerBaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        refreshTheme("onCreate")
    }

    override fun onResume() {
        super.onResume()
//        refreshTheme()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        refreshTheme("onConfigurationChanged")
    }

    fun refreshTheme(tag: String = "", forceRefresh: Boolean = false) {
        logE("refreshTheme: $tag")
        val theme = prefsHelper.selectedAppTheme
        val mode = when (theme) {
            DARK_THEME -> AppCompatDelegate.MODE_NIGHT_YES
            LIGHT_THEME -> AppCompatDelegate.MODE_NIGHT_NO
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
        AppCompatDelegate.setDefaultNightMode(mode)
        if (forceRefresh) {
            val mCallerIdSDKApplication: CallerIdSDKApplication? = try {
                this.application as CallerIdSDKApplication
            } catch (_: Exception) {
                null
            }
            mCallerIdSDKApplication?.onCallerThemeChanged(theme)
            recreate()
        }
    }
}