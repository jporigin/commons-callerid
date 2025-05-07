package com.origin.commons.callerid.extensions

import android.content.Context
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import com.origin.commons.callerid.helpers.SharedPreferencesHelper

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