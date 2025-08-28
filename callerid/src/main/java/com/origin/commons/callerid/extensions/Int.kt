package com.origin.commons.callerid.extensions

import android.content.res.Resources
import android.graphics.Color

val Int.dpToPx: Int get() = (toFloat() * Resources.getSystem().displayMetrics.density + 0.5f).toInt()

fun Int.formatedTime(): String {
    return if (this in 0..9) "0$this" else "" + this
}

fun formatedTime1(time: Int): String {
    return if (time in 0..9) "0$time" else "" + time
}

fun Int.withAlpha(alpha: Int): Int {
    return Color.argb(alpha, Color.red(this), Color.green(this), Color.blue(this))
}
