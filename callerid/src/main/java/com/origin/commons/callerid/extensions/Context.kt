package com.origin.commons.callerid.extensions

import android.app.ActivityManager
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.content.Intent
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import com.origin.commons.callerid.CallerIdSDKApplication
import com.origin.commons.callerid.helpers.SharedPreferencesHelper

fun Context.getOpenAppIntent(): Intent? {
    val callerIdSDKApplication = try {
        this.applicationContext as? CallerIdSDKApplication
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

fun Context.isActivityRunning(activityClass: Class<*>): Boolean {
    val activityManager = this.getSystemService(ACTIVITY_SERVICE) as ActivityManager
    val runningTasks = activityManager.appTasks
    for (task in runningTasks) {
        val info = task.taskInfo
        if (activityClass.canonicalName == info.baseActivity?.className || activityClass.canonicalName == info.topActivity?.className) {
            return true
        }
    }
    return false
}


val Context.prefsHelper: SharedPreferencesHelper get() = SharedPreferencesHelper.newInstance(this)

fun Context.setForegroundFromAttr(view: View, attributeId: Int, defaultDrawable: Int) {
    val typedValue = TypedValue()
    this.theme.resolveAttribute(attributeId, typedValue, true)
    val resId = typedValue.resourceId
    try {
        val drawableId = if (resId != 0) resId else defaultDrawable
        view.foreground = ContextCompat.getDrawable(this, drawableId)
    } catch (_: Exception) {
    }
}

fun Context.setBackgroundFromAttr(view: View, attributeId: Int, defaultColor: Int, defaultDrawable: Int = 0) {
    val typedValue = TypedValue()
    this.theme.resolveAttribute(attributeId, typedValue, true)
    val resId = typedValue.resourceId
    try {
        if (resId != 0) {
            view.setBackgroundResource(resId)
        } else {
            this.setBackgroundColorFromAttr(view, resId, defaultColor, defaultDrawable)
        }
    } catch (e: Exception) {
        this.setBackgroundColorFromAttr(view, resId, defaultColor, defaultDrawable)
    }
}

fun Context.setBackgroundColorFromAttr(view: View, attributeId: Int, defaultColor: Int, defaultDrawable: Int) {
    try {
        view.setBackgroundColor(ContextCompat.getColor(this, attributeId))
    } catch (e: Exception) {
        if (defaultDrawable != 0) {
            view.setBackgroundResource(defaultDrawable)
        } else {
            view.setBackgroundColor(ContextCompat.getColor(this@setBackgroundColorFromAttr, defaultColor))
        }
    }
}

fun Context.getColorFromAttr(attributeId: Int, defaultColor: Int): Int {
    val typedValue = TypedValue()
    this.theme.resolveAttribute(attributeId, typedValue, true)
    val resId = typedValue.resourceId
    val colorId = if (resId != 0) resId else defaultColor
    var color = 0
    try {
        if (resId != 0) {
            color = ContextCompat.getColor(this, colorId)
        }
    } catch (ignore: Exception) {
    }
    return color
}