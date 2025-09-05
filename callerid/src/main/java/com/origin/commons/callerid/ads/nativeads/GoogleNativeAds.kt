package com.origin.commons.callerid.ads.nativeads


import android.app.Activity
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Space
import android.widget.TextView
import com.google.android.gms.ads.nativead.NativeAd
import com.origin.commons.callerid.databinding.AdUnifiedNativeXlBinding
import com.origin.commons.callerid.databinding.AdUnifiedNativeXxlBinding
import com.origin.commons.callerid.databinding.ShimmerNativeXlBinding
import com.origin.commons.callerid.databinding.ShimmerNativeXxlBinding
import com.origin.commons.callerid.extensions.beGone
import com.origin.commons.callerid.extensions.beGoneIf
import com.origin.commons.callerid.extensions.beVisible
import com.origin.commons.callerid.extensions.dpToPx
import com.origin.commons.callerid.extensions.getScreenHeight

class GoogleNativeAds {
    private var skipAllNativeAds: Boolean = false
    private var showNativeShimmerLayout: Boolean = false

    private lateinit var rlMainGoogleNative: RelativeLayout
    private lateinit var flSpaceLayout: FrameLayout
    private lateinit var spMain: Space
    private lateinit var tvSpaceAds: TextView
    private lateinit var flShimmerGoogleNative: FrameLayout
    private lateinit var flGoogleNative: FrameLayout

    fun setupAdsViews(
        activity: Activity,
        skipAllNativeAds: Boolean,
        showNativeShimmerLayout: Boolean,
        rlMainGoogleNative: RelativeLayout, flSpaceLayout: FrameLayout,
        spMain: Space, tvSpaceAds: TextView,
        flShimmerGoogleNative: FrameLayout, flGoogleNative: FrameLayout
    ) {
        this.skipAllNativeAds = skipAllNativeAds
        this.showNativeShimmerLayout = showNativeShimmerLayout

        this.rlMainGoogleNative = rlMainGoogleNative
        this.flSpaceLayout = flSpaceLayout
        this.spMain = spMain
        this.tvSpaceAds = tvSpaceAds
        this.flShimmerGoogleNative = flShimmerGoogleNative
        this.flGoogleNative = flGoogleNative

        // Change Native Ads UI Layout
        activity.updateUIMainLayout(this.rlMainGoogleNative, this.tvSpaceAds)
    }

    private var mShimmerNativeXlBinding: ShimmerNativeXlBinding? = null
    fun initXlAd(activity: Activity) {
        rlMainGoogleNative.beGoneIf(skipAllNativeAds)
        if (skipAllNativeAds) {
            return
        }
        // Used_For_Shimmer_Layout
        if (showNativeShimmerLayout) {
            flSpaceLayout.beGone()
            flShimmerGoogleNative.beVisible()
            ShimmerNativeXlBinding.inflate(activity.layoutInflater).apply {
                mShimmerNativeXlBinding = this
                activity.updateUIShimmerXlLayout(this)
                flShimmerGoogleNative.removeAllViews()
                flShimmerGoogleNative.addView(this.root)
            }
        } else {
            flSpaceLayout.beVisible()
            flShimmerGoogleNative.beGone()
            val params: FrameLayout.LayoutParams = spMain.layoutParams as FrameLayout.LayoutParams
            params.height = 135.dpToPx
            params.width = FrameLayout.LayoutParams.MATCH_PARENT
            spMain.layoutParams = params
        }
    }

    fun onFailedToLoadXlAd() {
        try {
            mShimmerNativeXlBinding?.flShimmer?.hideShimmer()
        } catch (_: Exception) {
        }
    }

    fun showLoadedXlAd(activity: Activity, nativeAd: NativeAd) {
        flSpaceLayout.beGone()
        flShimmerGoogleNative.beGone()
        AdUnifiedNativeXlBinding.inflate(activity.layoutInflater).apply {
            activity.updateUINativeXlLayout(this)
            populateNativeXLAdView(nativeAd, this)
            flGoogleNative.removeAllViews()
            flGoogleNative.addView(this.root)
        }
    }


    private var mShimmerNativeXxlBinding: ShimmerNativeXxlBinding? = null
    fun initXxlAd(activity: Activity) {
        rlMainGoogleNative.beGoneIf(skipAllNativeAds)
        if (skipAllNativeAds) {
            return
        }
        // Used_For_Shimmer_Layout
        val mScHeight = if (activity.getScreenHeight() / defaultMediaHeightRatio > defaultMediaHeightPx) {
            (activity.getScreenHeight() / defaultMediaHeightRatio)
        } else {
            defaultMediaHeightPx
        }
        if (showNativeShimmerLayout) {
            flSpaceLayout.beGone()
            flShimmerGoogleNative.beVisible()
            ShimmerNativeXxlBinding.inflate(activity.layoutInflater).apply {
                mShimmerNativeXxlBinding = this
                activity.updateUIShimmerXxlLayout(this)
                flShimmerGoogleNative.removeAllViews()
                val params: LinearLayout.LayoutParams = this.iv2.layoutParams as LinearLayout.LayoutParams
                params.height = mScHeight.toInt()
                params.width = FrameLayout.LayoutParams.MATCH_PARENT
                this.iv2.layoutParams = params
                flShimmerGoogleNative.addView(this.root)
            }
        } else {
            flSpaceLayout.beVisible()
            flShimmerGoogleNative.beGone()
            val params: FrameLayout.LayoutParams = spMain.layoutParams as FrameLayout.LayoutParams
            params.height = mScHeight.toInt()
            params.width = FrameLayout.LayoutParams.MATCH_PARENT
            params.topMargin = 80.dpToPx
            params.bottomMargin = 62.dpToPx
            spMain.layoutParams = params
        }

    }

    fun onFailedToLoadXxlAd() {
        try {
            mShimmerNativeXxlBinding?.flShimmer?.hideShimmer()
        } catch (_: Exception) {
        }
    }

    fun showLoadedXxlAd(activity: Activity, nativeAd: NativeAd) {
        flSpaceLayout.beGone()
        flShimmerGoogleNative.beGone()
        AdUnifiedNativeXxlBinding.inflate(activity.layoutInflater).apply {
            activity.updateUINativeXxlLayout(this)
            activity.populateNativeXXLAdView(nativeAd, this)
            flGoogleNative.removeAllViews()
            flGoogleNative.addView(this.root)
        }
    }
}