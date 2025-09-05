package com.origin.commons.callerid.ads.bannerads

import android.app.Activity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.google.android.gms.ads.AdView
import com.origin.commons.callerid.databinding.ShimmerAdaptiveBannerLayoutBinding
import com.origin.commons.callerid.extensions.beGone
import com.origin.commons.callerid.extensions.beGoneIf
import com.origin.commons.callerid.extensions.beVisible
import com.origin.commons.callerid.extensions.getScreenWidthPx
import kotlin.math.roundToInt

class GoogleBannerAds {
    private var skipAllBannerAds: Boolean = false
    private var showBannerShimmerLayout: Boolean = false

    private lateinit var rlMainGoogleBanner: RelativeLayout
    private lateinit var flSpaceLayout: FrameLayout
    private lateinit var tvSpaceAds: TextView
    private lateinit var flShimmerGoogleBanner: FrameLayout
    private lateinit var flGoogleBanner: FrameLayout

    fun setupAdsViews(
        activity: Activity, skipAllBannerAds: Boolean, showBannerShimmerLayout: Boolean, rlMainGoogleBanner: RelativeLayout, flSpaceLayout: FrameLayout, tvSpaceAds: TextView,
        flShimmerGoogleBanner: FrameLayout, flGoogleBanner: FrameLayout
    ) {
        this.skipAllBannerAds = skipAllBannerAds
        this.showBannerShimmerLayout = showBannerShimmerLayout

        this.rlMainGoogleBanner = rlMainGoogleBanner
        this.flSpaceLayout = flSpaceLayout
        this.tvSpaceAds = tvSpaceAds
        this.flShimmerGoogleBanner = flShimmerGoogleBanner
        this.flGoogleBanner = flGoogleBanner

        // Change Banner Ads UI Layout
        activity.updateUIMainLayout(rlMainGoogleBanner, tvSpaceAds)
        setUpShimmerLoading(activity)
    }

    private fun setUpShimmerLoading(activity: Activity) {
        rlMainGoogleBanner.beGoneIf(skipAllBannerAds)
        if (skipAllBannerAds) {
            return
        }
        // Used_For_Shimmer_Layout
        if (showBannerShimmerLayout) {
            flSpaceLayout.beGone()
            flShimmerGoogleBanner.beVisible()
            ShimmerAdaptiveBannerLayoutBinding.inflate(activity.layoutInflater).apply {
                val params: RelativeLayout.LayoutParams = this.iv1.layoutParams as RelativeLayout.LayoutParams
                params.height = (activity.getScreenWidthPx() / 1.3f).roundToInt()
                params.width = RelativeLayout.LayoutParams.MATCH_PARENT
                this.iv1.layoutParams = params

                mShimmerBannerLayoutBinding = this
                activity.updateUIShimmerLayout(this)
                flShimmerGoogleBanner.removeAllViews()
                flShimmerGoogleBanner.addView(this.root)
            }
        } else {
            flSpaceLayout.beVisible()
            flShimmerGoogleBanner.beGone()
            val params: FrameLayout.LayoutParams = flSpaceLayout.layoutParams as FrameLayout.LayoutParams
            params.height = defaultBannerHeightPx
            params.width = FrameLayout.LayoutParams.MATCH_PARENT
            flSpaceLayout.layoutParams = params
        }
    }

    fun onAdLoaded(adView: AdView) {
        if (adView.parent != null) {
            (adView.parent as ViewGroup).removeView(adView)
        }
        flSpaceLayout.beGone()
        flShimmerGoogleBanner.beGone()
        flGoogleBanner.removeAllViews()
        flGoogleBanner.addView(adView)
    }

    private var mShimmerBannerLayoutBinding: ShimmerAdaptiveBannerLayoutBinding? = null
    fun onAdFailedToLoad() {
        try {
            mShimmerBannerLayoutBinding?.flShimmer?.hideShimmer()
        } catch (_: Exception) {
        }
    }
}