package com.origin.commons.callerid.ads.bannerads

import android.app.Activity
import android.widget.RelativeLayout
import android.widget.TextView
import com.origin.commons.callerid.R
import com.origin.commons.callerid.databinding.ShimmerBannerLayoutBinding
import com.origin.commons.callerid.extensions.dpToPx
import com.origin.commons.callerid.extensions.getColorFromAttr
import com.origin.commons.callerid.extensions.setBackgroundFromAttr

fun Activity.updateUIMainLayout(rlMainGoogleBanner: RelativeLayout, tvSpaceAds: TextView) {
    rlMainGoogleBanner.apply {
        this@updateUIMainLayout.setBackgroundFromAttr(this@apply, R.attr.bAdsBackground, R.color.b_bg_color)
    }
    tvSpaceAds.apply {
        setTextColor(this@updateUIMainLayout.getColorFromAttr(R.attr.bAdsTextColor, R.color.b_text_color))
    }
}

/**
 * Banner Ad
 */
val defaultBannerHeightPx: Int = 60.dpToPx
fun Activity.updateUIShimmerLayout(shimmerBannerLayoutBinding: ShimmerBannerLayoutBinding) {
    shimmerBannerLayoutBinding.apply {
        val views = listOf(tv1, tv2, tv3, tv4, iv1)
        views.forEach { view ->
            this@updateUIShimmerLayout.setBackgroundFromAttr(view, R.attr.bAdsShimmerColor, R.color.b_shimmer_color)
        }
    }
}


