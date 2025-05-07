package com.origin.commons.callerid.extensions

import android.content.res.Resources

val Int.dpToPx: Int get() = (toFloat() * Resources.getSystem().displayMetrics.density + 0.5f).toInt()
