package com.origin.commons.callerid.ads.utils

import android.app.Activity
import android.os.Build
import android.util.DisplayMetrics
import android.view.View
import com.google.android.gms.ads.AdSize

fun String.isSkippedAds(): Boolean {
    return this.isEmpty() || this.startsWith(" ") || this.startsWith("_") || this == "none" || this.endsWith(" ") || this.endsWith("_")
}

fun Activity.getBannerAdSize(view: View): AdSize {
    val adWidth: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val windowMetrics = this.windowManager.currentWindowMetrics
        val bounds = windowMetrics.bounds
        var adWidthPixels: Float = view.width.toFloat()
        if (adWidthPixels == 0f) {
            adWidthPixels = bounds.width().toFloat()
        }
        (adWidthPixels / this.resources.displayMetrics.density).toInt()
    } else {
        @Suppress("DEPRECATION")
        val display = windowManager.defaultDisplay
        val outMetrics = DisplayMetrics()
        @Suppress("DEPRECATION")
        display.getMetrics(outMetrics)
        val density = outMetrics.density
        var adWidthPixels: Float = view.width.toFloat()
        if (adWidthPixels == 0f) {
            adWidthPixels = outMetrics.widthPixels.toFloat()
        }
        (adWidthPixels / density).toInt()
    }
    return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth)
}






