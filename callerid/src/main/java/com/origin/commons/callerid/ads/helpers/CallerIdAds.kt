package com.origin.commons.callerid.ads.helpers

import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.origin.commons.callerid.ads.utils.getBannerAdSize
import com.origin.commons.callerid.extensions.getCurrentAdsType
import com.origin.commons.callerid.extensions.logE
import com.origin.commons.callerid.extensions.logEventE
import com.origin.commons.callerid.extensions.prefsHelper
import com.origin.commons.callerid.extensions.refreshCurrentAdsType

object CallerIdAds {
    var isLoadingAds = false
    private var isWaterfallRunning = false

    fun loadCallerIdAds(context: Context) {
        if (isLoadingAds || isWaterfallRunning) return
        val adsType = context.getCurrentAdsType()
        logE("callerAds::loadCallerIdAds::adsType:: $adsType")
        when (adsType) {
            1, 2 -> {
                if (!hasNativeAdAvailable()) {
                    isLoadingAds = true
                    isWaterfallRunning = true
                    preLoadNative(context, adsType, 0)
                }
            }

            3 -> {
                isLoadingAds = true
                isWaterfallRunning = true
                preLoadBanner(context, 0)
            }
        }
    }

    fun getIdsList(valueId: String?): List<String> {
        val gson = Gson()
        if (valueId.isNullOrEmpty()) {
            return emptyList()
        }
        return try {
            val type = object : TypeToken<List<String>>() {}.type
            val idList: List<String>? = gson.fromJson(valueId, type)
            idList ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }

    private var mBannerAdView: AdView? = null
    fun clearBannerAdView() {
        try {
            val parentView = mBannerAdView?.parent
            if (parentView is ViewGroup) {
                parentView.removeView(mBannerAdView)
            }
        } catch (_: Exception) {
        }
        mBannerAdView?.destroy()
        mBannerAdView = null
    }

    fun hasBannerAdAvailable(): Boolean = mBannerAdView != null
    fun getBannerAd(): AdView? = mBannerAdView

    private fun preLoadBanner(context: Context, index: Int) {
        val bannerAdsIds = getIdsList(context.prefsHelper.bannerIDsList)
        if (index >= bannerAdsIds.size) {
            logE("callerAds::glBannerAds::onFailure:done")
            isLoadingAds = false
            isWaterfallRunning = false
            context.refreshCurrentAdsType()
            onCallerAdsFailedToLoad()
            return
        }
        val currentAdUnitId = bannerAdsIds[index]
        clearBannerAdView()
        AdView(context).apply {
            adUnitId = currentAdUnitId
            setAdSize(context.getBannerAdSize())
            adListener = object : AdListener() {
                override fun onAdLoaded() {
                    super.onAdLoaded()
                    isLoadingAds = false
                    isWaterfallRunning = false
                    mBannerAdView = this@apply
                    onCallerBannerAdsLoaded(this@apply)
                    logE("callerAds::glBannerAds::adLoaded:")
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    super.onAdFailedToLoad(adError)
                    logE("callerAds::glBannerAds::adFailedToLoad:code:${adError.code} - message:${adError.message}")
                    if (adError.code == AdRequest.ERROR_CODE_NO_FILL) {
                        preLoadBanner(context, index + 1)
                    } else {
                        isLoadingAds = false
                        isWaterfallRunning = false
                        context.refreshCurrentAdsType()
                        onCallerAdsFailedToLoad()
                    }
                }

                override fun onAdImpression() {
                    super.onAdImpression()
                    onCallerAdsImpression()
                    logEventE("Showed_OGCallerAds_B")
                    logE("callerAds::glBannerAds::adShowed")
                }
            }
            val adRequest = AdRequest.Builder().build()
            loadAd(adRequest)
            isLoadingAds = true
            logE("callerAds::glBannerAds::request_new_ads")
        }
    }

    private var mNativeAd: NativeAd? = null
    fun clearNativeAdView() {
        mNativeAd?.destroy()
        mNativeAd = null
    }


    fun hasNativeAdAvailable(): Boolean = mNativeAd != null
    fun getNativeAd(): NativeAd? = mNativeAd

    private fun preLoadNative(context: Context, adsType: Int, index: Int) {
        val nativeAdsIds = when (adsType) {
            1 -> {
                getIdsList(context.prefsHelper.nativeBigIDsList)
            }

            2 -> {
                getIdsList(context.prefsHelper.nativeSmallIDsList)
            }

            else -> {
                emptyList()
            }
        }
        if (index >= nativeAdsIds.size) {
            logE("callerAds::glNativeAds::onFailure:done")
            isLoadingAds = false
            isWaterfallRunning = false
            context.refreshCurrentAdsType()
            onCallerAdsFailedToLoad()
            return
        }
        val currentAdUnitId = nativeAdsIds[index]
        clearNativeAdView()
        val adRequest = AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter::class.java, Bundle()).build()
        val builder = AdLoader.Builder(context, currentAdUnitId)
        builder.forNativeAd { ad ->
            isLoadingAds = false
            isWaterfallRunning = false
            mNativeAd = ad
            onCallerNativeAdsLoaded(ad)
            logE("callerAds::glNativeAds::adLoaded::$currentAdUnitId")
        }.withAdListener(object : AdListener() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                logE("callerAds::glNativeAds::adFailedToLoad:code:${adError.code} - message:${adError.message}")
                if (adError.code == AdRequest.ERROR_CODE_NO_FILL) {
                    preLoadNative(context, adsType, index + 1)
                } else {
                    isLoadingAds = false
                    isWaterfallRunning = false
                    context.refreshCurrentAdsType()
                    onCallerAdsFailedToLoad()
                }
            }

            override fun onAdImpression() {
                super.onAdImpression()
                onCallerAdsImpression()
                logEventE(buildString {
                    append("Showed_OGCallerAds_")
                    if (adsType == 1) {
                        append("N")
                    } else {
                        append("NB")
                    }
                })
                logE("callerAds::glNativeAds::adShowed")
            }
        }).build().loadAd(adRequest)
        logE("callerAds::glNativeAds::request_new_ads")
        isLoadingAds = true
    }

    interface CallerAdsListener {
        fun cAdsLoadedBanner(adView: AdView)
        fun cAdsLoadedNative(nativeAd: NativeAd)
        fun cAdsImpression()
        fun cAdsFailedToLoad()
    }

    private var mCallerAdsListeners = arrayListOf<CallerAdsListener>()
    fun registerCallerAdsListener(listener: CallerAdsListener) {
        synchronized(mCallerAdsListeners) {
            mCallerAdsListeners.add(listener) // prevents duplicate listeners
        }
    }

    fun unregisterCallerAdsListener(listener: CallerAdsListener) {
        synchronized(mCallerAdsListeners) {
            mCallerAdsListeners.remove(listener)
        }
    }

    private fun onCallerBannerAdsLoaded(adView: AdView) {
        val listenersCopy = synchronized(mCallerAdsListeners) {
            mCallerAdsListeners.toList()
        }
        listenersCopy.forEach { it.cAdsLoadedBanner(adView) }
    }

    private fun onCallerNativeAdsLoaded(nativeAd: NativeAd) {
        val listenersCopy = synchronized(mCallerAdsListeners) {
            mCallerAdsListeners.toList()
        }
        listenersCopy.forEach { it.cAdsLoadedNative(nativeAd) }
    }

    private fun onCallerAdsImpression() {
        val listenersCopy = synchronized(mCallerAdsListeners) {
            mCallerAdsListeners.toList()
        }
        listenersCopy.forEach { it.cAdsImpression() }
    }

    private fun onCallerAdsFailedToLoad() {
        val listenersCopy = synchronized(mCallerAdsListeners) {
            mCallerAdsListeners.toList()
        }
        listenersCopy.forEach { it.cAdsFailedToLoad() }
    }

}