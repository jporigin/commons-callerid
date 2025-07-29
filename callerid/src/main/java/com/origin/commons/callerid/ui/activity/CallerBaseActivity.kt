package com.origin.commons.callerid.ui.activity

import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.origin.commons.callerid.extensions.prefsHelper
import com.origin.commons.callerid.helpers.DARK_THEME
import com.origin.commons.callerid.helpers.LIGHT_THEME

abstract class CallerBaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        refreshTheme()
    }

    override fun onResume() {
        super.onResume()
        refreshTheme()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        refreshTheme()
    }

    fun refreshTheme(forceRefresh: Boolean = false) {
        val theme = prefsHelper.selectedAppTheme
        val mode = when (theme) {
            DARK_THEME -> AppCompatDelegate.MODE_NIGHT_YES
            LIGHT_THEME -> AppCompatDelegate.MODE_NIGHT_NO
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
        AppCompatDelegate.setDefaultNightMode(mode)
        if (forceRefresh) {
            recreate()
        }
    }
}