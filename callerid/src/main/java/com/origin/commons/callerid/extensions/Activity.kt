package com.origin.commons.callerid.extensions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.DisplayMetrics
import android.view.Display
import androidx.core.hardware.display.DisplayManagerCompat
import com.origin.commons.callerid.CallerIdSDKApplication

fun Activity.getOpenAppIntent(): Intent? {
    val callerIdSDKApplication = try {
        this.application as? CallerIdSDKApplication
    } catch (_: Exception) {
        null
    }
    val mClass1 = callerIdSDKApplication?.openClass1
    val mClass2High = callerIdSDKApplication?.openClass2High
    return when {
        mClass1 != null && isActivityRunning(mClass1.invoke()) -> {
            Intent(this@getOpenAppIntent, mClass1.invoke())
        }

        mClass2High != null -> {
            Intent(this@getOpenAppIntent, mClass2High.invoke())
        }
        else -> null
    }
}
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