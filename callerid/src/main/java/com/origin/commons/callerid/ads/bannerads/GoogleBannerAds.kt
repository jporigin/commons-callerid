package com.origin.commons.callerid.ads.bannerads

import android.app.Activity
import android.widget.FrameLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.origin.commons.callerid.ads.utils.isSkippedAds
import com.origin.commons.callerid.databinding.ShimmerBannerLayoutBinding
import com.origin.commons.callerid.extensions.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GoogleBannerAds {
    private var mGoogleBannerAds: AdView? = null

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
    }

    fun showAd(activity: Activity, adUnitId: String, adSize: AdSize) {
        rlMainGoogleBanner.beGoneIf(skipAllBannerAds)
        if (skipAllBannerAds) {
            return
        }
        // Used_For_Shimmer_Layout
        if (showBannerShimmerLayout) {
            flSpaceLayout.beGone()
            flShimmerGoogleBanner.beVisible()
            ShimmerBannerLayoutBinding.inflate(activity.layoutInflater).apply {
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
            flSpaceLayout.setLayoutParams(params)
        }
        // Used_For_Load_Banner_Ads
        CoroutineScope(Dispatchers.IO).launch {
            activity.runOnUiThread {
                showLoadedAd(activity, adUnitId, adSize, object : BannerAdsCallback {
                    override fun onBannerAdLoaded(adView: AdView) {
                        flSpaceLayout.beGone()
                        flShimmerGoogleBanner.beGone()
                        flGoogleBanner.removeAllViews()
                        flGoogleBanner.addView(adView)
                    }

                    override fun onBannerAdFailedToLoad() {}
                })
            }
        }
    }

    private fun showLoadedAd(activity: Activity, adUnitId: String, adSize: AdSize, bannerAdsCallback: BannerAdsCallback) {
        if (!adUnitId.isSkippedAds()) {
            AdView(activity).apply {
                this.adUnitId = adUnitId
                this.setAdSize(adSize)
                mGoogleBannerAds = this
                val build = AdRequest.Builder().build()
                this.loadAd(build)
                logE("glBannerAds::load:request_new_ads")
                this.adListener = object : AdListener() {
                    override fun onAdClicked() {
                        super.onAdClicked()
                        logE("glBannerAds::load:adClicked")
                    }

                    override fun onAdFailedToLoad(ad: LoadAdError) {
                        super.onAdFailedToLoad(ad)
                        bannerAdsCallback.onBannerAdFailedToLoad()
                        logE("glBannerAds::load:adFailedToLoad:: ${ad.message}")
                    }

                    override fun onAdLoaded() {
                        super.onAdLoaded()
                        logE("glBannerAds::load:adLoaded")
                        bannerAdsCallback.onBannerAdLoaded(this@apply)
                    }

                    override fun onAdImpression() {
                        super.onAdImpression()
                        logE("glBannerAds::load:adShowed")
                    }
                }
            }
        } else {
            bannerAdsCallback.onBannerAdFailedToLoad()
        }
    }

    fun pauseLoadedAds() {
        mGoogleBannerAds?.pause()
    }

    fun resumeLoadedAds() {
        mGoogleBannerAds?.resume()
    }

    fun destroyLoadedAds() {
        mGoogleBannerAds?.destroy()
    }
}