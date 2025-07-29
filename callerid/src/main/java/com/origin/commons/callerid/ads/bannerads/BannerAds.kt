package com.origin.commons.callerid.ads.bannerads

import android.app.Activity
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.origin.commons.callerid.R
import com.origin.commons.callerid.databinding.ShimmerAdaptiveBannerLayoutBinding
import com.origin.commons.callerid.extensions.dpToPx

fun Activity.updateUIMainLayout(rlMainGoogleBanner: RelativeLayout, tvSpaceAds: TextView) {
    rlMainGoogleBanner.apply {
        this@apply.setBackgroundColor(ContextCompat.getColor(this@updateUIMainLayout, R.color.call_theme_surfaceContainer))
    }
    tvSpaceAds.apply {
        setTextColor(ContextCompat.getColor(this@updateUIMainLayout, R.color.call_theme_onSurfaceVariant))
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
            view.setBackgroundColor(ContextCompat.getColor(this@updateUIShimmerLayout, R.color.call_theme_surfaceContainerHighest))
        }
    }
}