package com.origin.commons.callerid.extensions

import android.app.Activity
import android.os.Build
import android.util.DisplayMetrics
import android.view.Display
import androidx.core.hardware.display.DisplayManagerCompat

var mHeight: Int? = null
fun Activity.getScreenHeight(): Int {
    if (mHeight == null || mHeight == 0) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val defaultDisplay = DisplayManagerCompat.getInstance(this@getScreenHeight).getDisplay(Display.DEFAULT_DISPLAY)
            val displayContext = createDisplayContext(defaultDisplay!!)
            mHeight = displayContext.resources.displayMetrics.heightPixels
        } else {
            val displayMetrics = DisplayMetrics()
            @Suppress("DEPRECATION")
            this.windowManager.defaultDisplay.getMetrics(displayMetrics)
            mHeight = displayMetrics.heightPixels
        }
    }
    return mHeight ?: 0
}