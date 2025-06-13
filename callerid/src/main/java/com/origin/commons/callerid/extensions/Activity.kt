package com.origin.commons.callerid.extensions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.DisplayMetrics
import android.view.Display
import android.view.View
import android.view.WindowInsetsController
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.hardware.display.DisplayManagerCompat
import com.origin.commons.callerid.CallerIdSDKApplication

fun Activity.getOpenAppIntent(): Intent? {
    val callerIdSDKApplication = try {
        this.application as? CallerIdSDKApplication
    } catch (_: Exception) {
        null
    }
    val mClass1 = callerIdSDKApplication?.openClass1?.invoke()
    val mClass2High = callerIdSDKApplication?.openClass2High?.invoke()
    return when {
        mClass1 != null && isActivityRunning(mClass1) -> {
            Intent(this@getOpenAppIntent, mClass1)
        }

        mClass2High != null -> {
            Intent(this@getOpenAppIntent, mClass2High)
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

fun Activity.dismissKeyboard() {
    window.currentFocus?.let { focus ->
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(focus.windowToken, 0)
        focus.clearFocus()
    }
}
fun AppCompatActivity.hideSysNavigationBar() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        window.attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
    }
    this.hideSystemNavUI()
}
fun AppCompatActivity.hideSystemNavUI() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        this.window.setDecorFitsSystemWindows(false)
        this.window.insetsController?.hide(android.view.WindowInsets.Type.navigationBars())
        this.window.insetsController?.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    } else {
        this.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    }
}
