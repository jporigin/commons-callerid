package com.origin.commons.callerid.ads.nativeads


import android.app.Activity
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Space
import android.widget.TextView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.origin.commons.callerid.ads.utils.isSkippedAds
import com.origin.commons.callerid.databinding.AdUnifiedNativeXlBinding
import com.origin.commons.callerid.databinding.AdUnifiedNativeXxlBinding
import com.origin.commons.callerid.extensions.*
import com.origin.commons.callerid.extensions.logE
import com.origin.commons.callerid.databinding.ShimmerNativeXlBinding
import com.origin.commons.callerid.databinding.ShimmerNativeXxlBinding


class GoogleNativeAds {
    private var mGoogleNativeAds: NativeAd? = null
    private var googleNativeAsLoader: AdLoader? = null
    private fun loadAdsCallback(activity: Activity, adUnitId: String, adsCallback: NativeAdsCallback) {
        if (mGoogleNativeAds != null) {
            return
        }
        if (googleNativeAsLoader == null || !googleNativeAsLoader?.isLoading!!) {
            googleNativeAsLoader = AdLoader.Builder(activity, adUnitId).forNativeAd { nativeAd: NativeAd ->
                logE("glNativeAds::loadAd:adLoaded")
                if (activity.isDestroyed || activity.isFinishing || activity.isChangingConfigurations) {
                    nativeAd.destroy()
                    return@forNativeAd
                }
                mGoogleNativeAds?.destroy()
                mGoogleNativeAds = nativeAd
                adsCallback.onNativeAdLoaded(nativeAd)
            }.withAdListener(object : AdListener() {
                override fun onAdClicked() {
                    super.onAdClicked()
                }

                override fun onAdImpression() {
                    super.onAdImpression()
                    logE("glNativeAds::loadAd:adShowed")
                    activity.logEventE("Showed_OGCallerAds_N")
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    logE("glNativeAds::loadAd:adFailedToLoad: ${adError.message}")
                    adsCallback.onNativeAdFailedToLoad()
                }
            }).withNativeAdOptions(NativeAdOptions.Builder().build()).build()
            googleNativeAsLoader?.loadAd(AdRequest.Builder().build())
            logE("glNativeAds::loadAd:Request")
        }
    }

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


    fun showXlAd(activity: Activity, adUnitId: String) {
        rlMainGoogleNative.beGoneIf(skipAllNativeAds)
        if (skipAllNativeAds) {
            return
        }
        // Used_For_Shimmer_Layout
        if (showNativeShimmerLayout) {
            flSpaceLayout.beGone()
            flShimmerGoogleNative.beVisible()
            ShimmerNativeXlBinding.inflate(activity.layoutInflater).apply {
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
            spMain.setLayoutParams(params)
        }

        if (mGoogleNativeAds != null) {
            activity.showLoadedXlAd()
        } else {
            if (!adUnitId.isSkippedAds()) {
                loadAdsCallback(activity, adUnitId, object : NativeAdsCallback {
                    override fun onNativeAdLoaded(nativeAd: NativeAd) {
                        activity.showLoadedXlAd()
                    }

                    override fun onNativeAdFailedToLoad() {
                    }
                })
            }
        }
    }

    private fun Activity.showLoadedXlAd() {
        mGoogleNativeAds?.let { googleNativeAds ->
            flSpaceLayout.beGone()
            flShimmerGoogleNative.beGone()
            AdUnifiedNativeXlBinding.inflate(this@showLoadedXlAd.layoutInflater).apply {
                this@showLoadedXlAd.updateUINativeXlLayout(this)
                populateNativeXLAdView(googleNativeAds, this)
                flGoogleNative.removeAllViews()
                flGoogleNative.addView(this.root)
            }
        }
    }

    fun showXxlAd(activity: Activity, adUnitId: String) {
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
            spMain.setLayoutParams(params)
        }

        if (mGoogleNativeAds != null) {
            activity.showLoadedXxlAd()
        } else {
            if (!adUnitId.isSkippedAds()) {
                loadAdsCallback(activity, adUnitId, object : NativeAdsCallback {
                    override fun onNativeAdLoaded(nativeAd: NativeAd) {
                        activity.showLoadedXxlAd()
                    }

                    override fun onNativeAdFailedToLoad() {
                    }
                })
            }
        }
    }

    private fun Activity.showLoadedXxlAd() {
        mGoogleNativeAds?.let { googleNativeAds ->
            flSpaceLayout.beGone()
            flShimmerGoogleNative.beGone()
            AdUnifiedNativeXxlBinding.inflate(this@showLoadedXxlAd.layoutInflater).apply {
                this@showLoadedXxlAd.updateUINativeXxlLayout(this)
                this@showLoadedXxlAd.populateNativeXXLAdView(googleNativeAds, this)
                flGoogleNative.removeAllViews()
                flGoogleNative.addView(this.root)
            }
        }
    }


    fun destroyLoadedAds() {
        mGoogleNativeAds?.destroy()
    }
}