package com.origin.commons.callerid.ads.nativeads

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.OnHierarchyChangeListener
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.origin.commons.callerid.R
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.origin.commons.callerid.databinding.AdUnifiedNativeXlBinding
import com.origin.commons.callerid.databinding.AdUnifiedNativeXxlBinding
import com.origin.commons.callerid.databinding.ShimmerNativeXlBinding
import com.origin.commons.callerid.databinding.ShimmerNativeXxlBinding
import com.origin.commons.callerid.extensions.*

fun Activity.updateUIMainLayout(rlMainGoogleNative: RelativeLayout, tvSpaceAds: TextView) {
    rlMainGoogleNative.apply {
        this@updateUIMainLayout.setBackgroundFromAttr(this@apply, R.attr.nAdsBackground, R.color.n_bg_color)
    }
    tvSpaceAds.apply {
        setTextColor(this@updateUIMainLayout.getColorFromAttr(R.attr.nAdsTextColor, R.color.n_text_color))
    }
}

/**
 * Xl Native Ad
 */
fun Activity.updateUIShimmerXlLayout(shimmerNativeXlBinding: ShimmerNativeXlBinding) {
    shimmerNativeXlBinding.apply {
        val views = listOf(tv1, tv2, tv3, tv4, iv1)
        views.forEach { view ->
            this@updateUIShimmerXlLayout.setBackgroundFromAttr(view, R.attr.nAdsShimmerColor, R.color.n_shimmer_color)
        }
    }
}

fun Activity.updateUINativeXlLayout(unifiedNativeXlBinding: AdUnifiedNativeXlBinding) {
    unifiedNativeXlBinding.apply {
        val adsTxtViews = listOf(adHeadline, adBody)
        adsTxtViews.forEach { view ->
            view.setTextColor(this@updateUINativeXlLayout.getColorFromAttr(R.attr.nAdsTextColor, R.color.n_text_color))
        }
        this@updateUINativeXlLayout.setBackgroundFromAttr(adAttribution, R.attr.nAdsAttributionBg, R.color.n_attribution_color)
        adAttribution.setTextColor(this@updateUINativeXlLayout.getColorFromAttr(R.attr.nAdsCallActionTvColor, R.color.n_attribution_text_color))

        this@updateUINativeXlLayout.setBackgroundFromAttr(adCallToAction, R.attr.nAdsCallActionBg, 0, R.drawable.native_call_action_bg)
        this@updateUINativeXlLayout.setForegroundFromAttr(adCallToAction, R.attr.nAdsCallActionFg, R.drawable.ads_call_action_ripple)
        adCallToAction.setTextColor(this@updateUINativeXlLayout.getColorFromAttr(R.attr.nAdsCallActionTvColor, R.color.n_call_action_tv_color))
    }
}

fun populateNativeXLAdView(nativeAd: NativeAd, unifiedAdBinding: AdUnifiedNativeXlBinding) {
    val nativeAdView = unifiedAdBinding.root

    // Set ad assets.
    nativeAdView.iconView = unifiedAdBinding.adAppIcon
    nativeAdView.headlineView = unifiedAdBinding.adHeadline
    nativeAdView.bodyView = unifiedAdBinding.adBody
    nativeAdView.callToActionView = unifiedAdBinding.adCallToAction

    // The headline and media content are guaranteed to be in every UnifiedNativeAd.
    unifiedAdBinding.adHeadline.text = nativeAd.headline

    // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
    // check before trying to display them.
    if (nativeAd.body == null) {
        unifiedAdBinding.adBody.beInvisible()
    } else {
        unifiedAdBinding.adBody.beVisible()
        unifiedAdBinding.adBody.text = nativeAd.body
    }

    if (nativeAd.callToAction == null) {
        unifiedAdBinding.adCallToAction.beInvisible()
    } else {
        unifiedAdBinding.adCallToAction.beVisible()
        unifiedAdBinding.adCallToAction.text = nativeAd.callToAction
    }

    if (nativeAd.icon == null) {
        unifiedAdBinding.adAppIcon.beGone()
    } else {
        unifiedAdBinding.adAppIcon.setImageDrawable(nativeAd.icon?.drawable)
        unifiedAdBinding.adAppIcon.beVisible()
    }
    // This method tells the Google Mobile Ads SDK that you have finished populating your
    // native ad view with this native ad.
    nativeAdView.setNativeAd(nativeAd)
}

/**
 * Xxl Native Ad
 */
const val defaultMediaHeightRatio: Float = 4.5f
val defaultMediaHeightPx: Int = 170.dpToPx

fun Activity.updateUIShimmerXxlLayout(shimmerNativeXxlBinding: ShimmerNativeXxlBinding) {
    shimmerNativeXxlBinding.apply {
        val views = listOf(tv1, tv2, tv3, tv4, iv1, iv2)
        views.forEach { view ->
            this@updateUIShimmerXxlLayout.setBackgroundFromAttr(view, R.attr.nAdsShimmerColor, R.color.n_shimmer_color)
        }
    }
}

fun Activity.updateUINativeXxlLayout(unifiedNativeXxlBinding: AdUnifiedNativeXxlBinding) {
    unifiedNativeXxlBinding.apply {
        val adsTxtViews = listOf(adHeadline, adBody)
        adsTxtViews.forEach { view ->
            view.setTextColor(this@updateUINativeXxlLayout.getColorFromAttr(R.attr.nAdsTextColor, R.color.n_text_color))
        }
        this@updateUINativeXxlLayout.setBackgroundFromAttr(adAttribution, R.attr.nAdsAttributionBg, R.color.n_attribution_color)
        adAttribution.setTextColor(this@updateUINativeXxlLayout.getColorFromAttr(R.attr.nAdsCallActionTvColor, R.color.n_attribution_text_color))

        this@updateUINativeXxlLayout.setBackgroundFromAttr(adCallToAction, R.attr.nAdsCallActionBg, 0, R.drawable.native_call_action_bg)
        this@updateUINativeXxlLayout.setForegroundFromAttr(adCallToAction, R.attr.nAdsCallActionFg, R.drawable.ads_call_action_ripple)
        adCallToAction.setTextColor(this@updateUINativeXxlLayout.getColorFromAttr(R.attr.nAdsCallActionTvColor, R.color.n_call_action_tv_color))
    }
}

fun Activity.populateNativeXXLAdView(nativeAd: NativeAd, unifiedAdBinding: AdUnifiedNativeXxlBinding) {
    val nativeAdView = unifiedAdBinding.root

    // Set ad assets.
    nativeAdView.iconView = unifiedAdBinding.adAppIcon
    nativeAdView.headlineView = unifiedAdBinding.adHeadline
    nativeAdView.bodyView = unifiedAdBinding.adBody
    nativeAdView.callToActionView = unifiedAdBinding.adCallToAction
    // Set the media view.
    val mScHeight = if (this.getScreenHeight() / defaultMediaHeightRatio > defaultMediaHeightPx) {
        (this.getScreenHeight() / defaultMediaHeightRatio)
    } else {
        defaultMediaHeightPx
    }
    val mediaView: MediaView = unifiedAdBinding.adMedia
    val params = mediaView.layoutParams
    params.height = mScHeight.toInt()
    params.width = ViewGroup.LayoutParams.MATCH_PARENT
    mediaView.layoutParams = params
    mediaView.setImageScaleType(ImageView.ScaleType.CENTER_CROP)
    mediaView.setOnHierarchyChangeListener(object : OnHierarchyChangeListener {
        override fun onChildViewAdded(parent: View, child: View) {
            if (child is ImageView) {
                child.adjustViewBounds = true
            }
        }

        override fun onChildViewRemoved(parent: View, child: View) {
        }
    })
    nativeAdView.mediaView = mediaView

    // The headline and media content are guaranteed to be in every UnifiedNativeAd.
    unifiedAdBinding.adHeadline.text = nativeAd.headline
    nativeAd.mediaContent?.let { unifiedAdBinding.adMedia.mediaContent = it }

    // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
    // check before trying to display them.
    if (nativeAd.body == null) {
        unifiedAdBinding.adBody.beInvisible()
    } else {
        unifiedAdBinding.adBody.beVisible()
        unifiedAdBinding.adBody.text = nativeAd.body
    }

    if (nativeAd.callToAction == null) {
        unifiedAdBinding.adCallToAction.beInvisible()
    } else {
        unifiedAdBinding.adCallToAction.beVisible()
        unifiedAdBinding.adCallToAction.text = nativeAd.callToAction
    }

    if (nativeAd.icon == null) {
        unifiedAdBinding.adAppIcon.beGone()
    } else {
        unifiedAdBinding.adAppIcon.setImageDrawable(nativeAd.icon?.drawable)
        unifiedAdBinding.adAppIcon.beVisible()
    }
    // This method tells the Google Mobile Ads SDK that you have finished populating your
    // native ad view with this native ad.
    nativeAdView.setNativeAd(nativeAd)
}