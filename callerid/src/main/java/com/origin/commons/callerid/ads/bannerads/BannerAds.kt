package com.origin.commons.callerid.ads.bannerads

import android.app.Activity
import android.widget.RelativeLayout
import android.widget.TextView
import com.origin.commons.callerid.R
import com.origin.commons.callerid.databinding.ShimmerAdaptiveBannerLayoutBinding
import com.origin.commons.callerid.extensions.dpToPx
import com.origin.commons.callerid.extensions.resolveThemeColor

fun Activity.updateUIMainLayout(rlMainGoogleBanner: RelativeLayout, tvSpaceAds: TextView) {
    rlMainGoogleBanner.apply {
        this@apply.setBackgroundColor(resolveThemeColor(R.attr.callThemeAdsBgColor))
    }
    tvSpaceAds.apply {
        setTextColor(resolveThemeColor(R.attr.callThemeAdsTextColorVariant))
    }
}

/**
 * Banner Ad
 */
val defaultBannerHeightPx: Int = 60.dpToPx
fun Activity.updateUIShimmerLayout(shimmerBannerLayoutBinding: ShimmerAdaptiveBannerLayoutBinding) {
    shimmerBannerLayoutBinding.apply {
        val views = listOf(tv1, iv1)
        views.forEach { view ->
            view.setBackgroundColor(resolveThemeColor(R.attr.callThemeAdsBgColorHigh))
        }
    }
}