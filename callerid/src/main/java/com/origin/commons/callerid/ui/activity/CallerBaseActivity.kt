package com.origin.commons.callerid.ui.activity

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.origin.commons.callerid.CallerIdSDKApplication
import com.origin.commons.callerid.R
import com.origin.commons.callerid.extensions.enableThemeEdgeToEdge
import com.origin.commons.callerid.extensions.isDarkMode
import com.origin.commons.callerid.extensions.logE
import com.origin.commons.callerid.extensions.prefsHelper
import com.origin.commons.callerid.extensions.resolveThemeColor
import com.origin.commons.callerid.model.ThemeConfig

abstract class CallerBaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(this, prefsHelper.themeConfig)
    }
    fun refreshTheme(tag: String = "", forceRefresh: Boolean = false) {
        logE("refreshTheme: $tag")
        val theme = prefsHelper.themeConfig
        logE("theme: $theme")
        setTheme(this, theme)
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
            ContextWrapper(it)
        })
    }

    private fun setTheme(context: Context, theme: ThemeConfig) {
        val isDarkMode = when(theme) {
            ThemeConfig.SYSTEM_THEME -> context.isDarkMode()
            ThemeConfig.LIGHT_THEME -> false
            ThemeConfig.DARK_THEME -> true
        }
        setTheme(getThemeStyle(isDarkMode))
        enableThemeEdgeToEdge(resolveThemeColor(R.attr.callThemeSurfaceContainerLowest), Color.TRANSPARENT, isDarkMode)
    }

    private fun getThemeStyle(isDarkMode: Boolean = false) = when (isDarkMode) {
        true -> R.style.Caller_Main_Theme_Dark
        false -> R.style.Caller_Main_Theme_Light
    }

}