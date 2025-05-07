package com.origin.commons.callerid.ads.bannerads

import com.google.android.gms.ads.AdView

interface BannerAdsCallback {
    fun onBannerAdLoaded(adView: AdView)
    fun onBannerAdFailedToLoad()
}