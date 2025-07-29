package com.origin.commons.callerid.ads.utils

import android.content.Context
import com.google.android.gms.ads.AdSize
import com.origin.commons.callerid.extensions.getScreenWidthDp

fun Context.getBannerAdSize(): AdSize {
    return AdSize.getCurrentOrientationInlineAdaptiveBannerAdSize(this, getScreenWidthDp())
}