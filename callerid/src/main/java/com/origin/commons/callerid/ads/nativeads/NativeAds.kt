package com.origin.commons.callerid.ads.nativeads

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.OnHierarchyChangeListener
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
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
        this@apply.setBackgroundColor(ContextCompat.getColor(this@updateUIMainLayout, R.color.call_theme_adsBgColor))
    }
    tvSpaceAds.apply {
        setTextColor(ContextCompat.getColor(this@updateUIMainLayout, R.color.call_theme_adsTextColorVariant))
    }
}

/**
 * Xl Native Ad
 */
fun Activity.updateUIShimmerXlLayout(shimmerNativeXlBinding: ShimmerNativeXlBinding) {
    shimmerNativeXlBinding.apply {
        val views = listOf(tv1, tv2, tv3, tv4, iv1)
        views.forEach { view ->
            view.setBackgroundColor(ContextCompat.getColor(this@updateUIShimmerXlLayout, R.color.call_theme_adsBgColorHigh))
        }
    }
}

fun Activity.updateUINativeXlLayout(unifiedNativeXlBinding: AdUnifiedNativeXlBinding) {
    unifiedNativeXlBinding.apply {
        val adsTxtViews = listOf(adHeadline, adBody)
        adsTxtViews.forEach { view ->
            view.setTextColor(ContextCompat.getColor(this@updateUINativeXlLayout, R.color.call_theme_adsTextColor))
        }
        adAttribution.setBackgroundResource(R.drawable.ads_attribution_bg)
        adAttribution.setTextColor(ContextCompat.getColor(this@updateUINativeXlLayout, R.color.call_theme_adsOnPrimary))

        adCallToAction.setBackgroundResource(R.drawable.ads_call_action_bg)
        adCallToAction.foreground = ContextCompat.getDrawable(this@updateUINativeXlLayout, R.drawable.ads_call_action_ripple)

        adCallToAction.setTextColor(ContextCompat.getColor(this@updateUINativeXlLayout, R.color.call_theme_adsOnPrimary))
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

    if (nativeAd.icon == null && nativeAd.icon?.drawable == null) {
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
            view.setBackgroundColor(ContextCompat.getColor(this@updateUIShimmerXxlLayout, R.color.call_theme_adsBgColorHigh))
        }
    }
}

fun Activity.updateUINativeXxlLayout(unifiedNativeXxlBinding: AdUnifiedNativeXxlBinding) {
    unifiedNativeXxlBinding.apply {
        val adsTxtViews = listOf(adHeadline, adBody)
        adsTxtViews.forEach { view ->
            view.setTextColor(ContextCompat.getColor(this@updateUINativeXxlLayout, R.color.call_theme_adsTextColor))
        }
        adAttribution.setBackgroundResource(R.drawable.ads_attribution_bg)
        adAttribution.setTextColor(ContextCompat.getColor(this@updateUINativeXxlLayout, R.color.call_theme_adsOnPrimary))


        adCallToAction.setBackgroundResource(R.drawable.ads_call_action_bg)
        adCallToAction.foreground = ContextCompat.getDrawable(this@updateUINativeXxlLayout, R.drawable.ads_call_action_ripple)


        adCallToAction.setTextColor(ContextCompat.getColor(this@updateUINativeXxlLayout, R.color.call_theme_adsOnPrimary))
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

    if (nativeAd.icon == null && nativeAd.icon?.drawable == null) {
        unifiedAdBinding.adAppIcon.beGone()
    } else {
        unifiedAdBinding.adAppIcon.setImageDrawable(nativeAd.icon?.drawable)
        unifiedAdBinding.adAppIcon.beVisible()
    }
    // This method tells the Google Mobile Ads SDK that you have finished populating your
    // native ad view with this native ad.
    nativeAdView.setNativeAd(nativeAd)
}