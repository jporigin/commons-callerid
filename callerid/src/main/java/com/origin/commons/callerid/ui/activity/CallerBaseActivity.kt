package com.origin.commons.callerid.ui.activity

import android.content.Context
import android.content.ContextWrapper
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.origin.commons.callerid.CallerIdSDKApplication
import com.origin.commons.callerid.extensions.logE
import com.origin.commons.callerid.extensions.prefsHelper
import com.origin.commons.callerid.model.ThemeConfig

abstract class CallerBaseActivity : AppCompatActivity() {
    fun refreshTheme(tag: String = "", forceRefresh: Boolean = false) {
        logE("refreshTheme: $tag")
        val theme = prefsHelper.themeConfig
        logE("theme: $theme")
        val mode = when (theme) {
            ThemeConfig.LIGHT_THEME -> AppCompatDelegate.MODE_NIGHT_NO
            ThemeConfig.DARK_THEME -> AppCompatDelegate.MODE_NIGHT_YES
            ThemeConfig.SYSTEM_THEME -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
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

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase?.let {
            when(it.prefsHelper.themeConfig) {
                ThemeConfig.SYSTEM_THEME -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                ThemeConfig.LIGHT_THEME -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                ThemeConfig.DARK_THEME -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            ContextWrapper(it)
        })
    }

}