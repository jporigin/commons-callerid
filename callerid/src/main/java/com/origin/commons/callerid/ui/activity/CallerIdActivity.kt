package com.origin.commons.callerid.ui.activity

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.material.tabs.TabLayoutMediator
import com.origin.commons.callerid.CallerIdSDKApplication
import com.origin.commons.callerid.R
import com.origin.commons.callerid.ads.bannerads.GoogleBannerAds
import com.origin.commons.callerid.ads.helpers.CallerIdAds
import com.origin.commons.callerid.ads.nativeads.GoogleNativeAds
import com.origin.commons.callerid.databinding.ActivityCallerIdBinding
import com.origin.commons.callerid.extensions.beGone
import com.origin.commons.callerid.extensions.beInvisible
import com.origin.commons.callerid.extensions.dpToPx
import com.origin.commons.callerid.extensions.getCurrentAdsType
import com.origin.commons.callerid.extensions.getDefaultAppIcon
import com.origin.commons.callerid.extensions.getOpenAppIntent
import com.origin.commons.callerid.extensions.hideSysNavigationBar
import com.origin.commons.callerid.extensions.logE
import com.origin.commons.callerid.extensions.logEventE
import com.origin.commons.callerid.extensions.prefsHelper
import com.origin.commons.callerid.ui.fragment.HomeFragment
import com.origin.commons.callerid.ui.fragment.MessageFragment
import com.origin.commons.callerid.ui.fragment.MoreFragment
import com.origin.commons.callerid.ui.fragment.ReminderFragment
import com.origin.commons.callerid.helpers.HomeKeyWatcher


class CallerIdActivity : CallerBaseActivity(), CallerIdAds.CallerAdsListener, HomeKeyWatcher.OnHomeAndRecentsListener {
    private val _binding by lazy {
        ActivityCallerIdBinding.inflate(layoutInflater)
    }

    private val phoneNumber by lazy { intent.getStringExtra("phoneNumber") }
    private val time by lazy { intent.getStringExtra("time") }
    private val duration by lazy { intent.getStringExtra("duration") }
    private val callType by lazy { intent.getStringExtra("callType") }

    var watcher: HomeKeyWatcher? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge(SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT), SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT))
        setContentView(_binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(_binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 5.dpToPx)
            insets
        }

        this@CallerIdActivity.hideSysNavigationBar()
        try {
            _binding.tvCallComingTime.text = time
        } catch (_: Exception) {
        }

        try {
            _binding.tvCallDuration.text = duration
        } catch (_: Exception) {
        }

        try {
            _binding.phoneNumber.text = phoneNumber
        } catch (_: Exception) {
        }

        try {
            _binding.tvCallType.text = callType
        } catch (_: Exception) {
        }

        setUpTabPagerAdapter()
        setUpAds()

        try {
            val customAppLogoResId = (applicationContext as? CallerIdSDKApplication)?.appLogo
            if (customAppLogoResId != null && customAppLogoResId != 0) {
                try {
                    _binding.ivLogo.setImageResource(customAppLogoResId)
                } catch (_: Resources.NotFoundException) {
                    getDefaultAppIcon()?.let {
                        _binding.ivLogo.setImageDrawable(it)
                    }
                }
            } else {
                getDefaultAppIcon()?.let {
                    _binding.ivLogo.setImageDrawable(it)
                }
            }
        } catch (_: Exception) {
        }

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

        if (watcher == null) {
            watcher = HomeKeyWatcher(this@CallerIdActivity).apply {
                setListener(this@CallerIdActivity)
                startWatching()
            }
        }


        val defEvent = "Showed_OGCallerScreen"
        logEventE(defEvent)
        this@CallerIdActivity.prefsHelper.apply {
            val eventName = buildString {
                append(defEvent)
                if (isMissedCallFeatureEnable) append("_m")
                if (isCompleteCallFeatureEnable) append("_c")
                if (isNoAnswerFeatureEnable) append("_n")
            }
            logEventE(eventName)
        }
    }


    private fun finishMyActNRemoveTask() {
        Handler(Looper.getMainLooper()).post {
            val comp = intent?.component
            if (!isFinishing && comp != null) {
                try {
                    finishAndRemoveTask()
                } catch (e: Exception) {
                    e.printStackTrace()
                    finish()
                }
            } else {
                finish()
            }
        }
    }

    override fun onHomePressed() {
        finishMyActNRemoveTask()
    }

    override fun onRecentsPressed() {
        finishMyActNRemoveTask()
    }
    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            logEventE("OGCallerScreen_back")
            finishMyActNRemoveTask()
        }
    }
    /*
    *
    * */


    override fun onDestroy() {
        mHomeFragment = null
        mMessageFragment = null
        mReminderFragment = null
        mMoreFragment = null
        CallerIdAds.unregisterCallerAdsListener(this@CallerIdActivity)
        CallerIdAds.clearBannerAdView()
        CallerIdAds.clearNativeAdView()
        watcher?.stopWatching()
        watcher = null
        super.onDestroy()
    }

    private fun setUpAds() {
        val adsType = this.getCurrentAdsType()
        logE("callerAds::setUpAds:$adsType ..")
        when (adsType) {
            3 -> {
                _binding.incAdsView.rlMainGoogleAds.beInvisible()
                setupGoogleBannerAds()
                logE("callerAds::setUpAds:$adsType :: ${CallerIdAds.hasBannerAdAvailable()}")
                if (!CallerIdAds.hasBannerAdAvailable()) {
                    CallerIdAds.loadCallerIdAds(this@CallerIdActivity)
                } else {
                    CallerIdAds.getBannerAd()?.let { cAdsLoadedBanner(it) }
                }
            }

            2 -> {
                _binding.incAdsView.rlMainGoogleAds.beInvisible()
                setupGoogleNativeAds()
                mGoogleNativeAds?.initXlAd(this@CallerIdActivity)
                logE("callerAds::setUpAds:$adsType :: ${CallerIdAds.hasNativeAdAvailable()}")
                if (!CallerIdAds.hasNativeAdAvailable()) {
                    CallerIdAds.loadCallerIdAds(this@CallerIdActivity)
                } else {
                    CallerIdAds.getNativeAd()?.let { cAdsLoadedNative(it) }
                }
            }

            1 -> {
                _binding.incAdsView.rlMainGoogleAds.beInvisible()
                setupGoogleNativeAds()
                mGoogleNativeAds?.initXxlAd(this@CallerIdActivity)
                logE("callerAds::setUpAds:$adsType :: ${CallerIdAds.hasNativeAdAvailable()}")
                if (!CallerIdAds.hasNativeAdAvailable()) {
                    CallerIdAds.loadCallerIdAds(this@CallerIdActivity)
                } else {
                    CallerIdAds.getNativeAd()?.let { cAdsLoadedNative(it) }
                }
            }

            else -> {
                _binding.incAdsView.rlMainGoogleAds.beGone()
            }
        }
        CallerIdAds.registerCallerAdsListener(this@CallerIdActivity)
    }


    private fun setUpTabPagerAdapter() {
        _binding.vpTab.apply {
            this.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    var view = currentFocus
                    if (view == null) {
                        view = View(this@CallerIdActivity)
                    }
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                }
            })
            adapter = TabPagerAdapter(this@CallerIdActivity)
            offscreenPageLimit = 4
        }
        TabLayoutMediator(_binding.tabLayout, _binding.vpTab) { tab, position ->
            tab.setIcon(
                when (position) {
                    0 -> R.drawable.ci_menu
                    1 -> R.drawable.ci_message
                    2 -> R.drawable.ci_notification
                    3 -> R.drawable.ci_more
                    else -> R.drawable.ci_menu
                }
            )
        }.attach()

        _binding.cvLogo.setOnClickListener {
            logEventE("OGCallerScreen_logo_cl")
            this@CallerIdActivity.getOpenAppIntent()?.let { intent ->
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                this@CallerIdActivity.startActivity(intent)
                this@CallerIdActivity.finish()
            }
        }

        _binding.llCall.setOnClickListener {
            logEventE("OGCallerScreen_call_cl")
            makePhoneCall(this)
        }
    }


    private fun makePhoneCall(context: Context) {
        val intent = Intent(Intent.ACTION_DIAL)
        context.startActivity(intent)
        finishMyActNRemoveTask()
    }

    private var mHomeFragment: HomeFragment? = null
    private var mMessageFragment: MessageFragment? = null
    private var mReminderFragment: ReminderFragment? = null
    private var mMoreFragment: MoreFragment? = null

    inner class TabPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

        override fun getItemCount(): Int = 4

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> {
                    mHomeFragment ?: HomeFragment().apply { mHomeFragment = this }
                }

                1 -> {
                    mMessageFragment ?: MessageFragment().apply { mMessageFragment = this }
                }

                2 -> {
                    mReminderFragment ?: ReminderFragment().apply { mReminderFragment = this }
                }

                3 -> {
                    mMoreFragment ?: MoreFragment().apply { mMoreFragment = this }
                }

                else -> {
                    mHomeFragment ?: HomeFragment().apply { mHomeFragment = this }
                }
            }
        }
    }

    private var mGoogleBannerAds: GoogleBannerAds? = null
    private fun setupGoogleBannerAds() {
        if (mGoogleBannerAds == null) {
            GoogleBannerAds().apply {
                mGoogleBannerAds = this
                setupAdsViews(
                    this@CallerIdActivity, skipAllBannerAds = false,
                    showBannerShimmerLayout = true, _binding.incAdsView.rlMainGoogleAds, _binding.incAdsView.flSpaceLayout, _binding.incAdsView.tvSpaceAds,
                    _binding.incAdsView.flShimmerGoogleAds, _binding.incAdsView.flGoogleAds
                )
            }
        }
    }

    //
    private var mGoogleNativeAds: GoogleNativeAds? = null
    private fun setupGoogleNativeAds() {
        if (mGoogleNativeAds == null) {
            GoogleNativeAds().apply {
                mGoogleNativeAds = this
                setupAdsViews(
                    this@CallerIdActivity, skipAllNativeAds = false, showNativeShimmerLayout = true,
                    _binding.incAdsView.rlMainGoogleAds, _binding.incAdsView.flSpaceLayout,
                    _binding.incAdsView.spMain, _binding.incAdsView.tvSpaceAds,
                    _binding.incAdsView.flShimmerGoogleAds, _binding.incAdsView.flGoogleAds
                )
            }
        }
    }


    override fun cAdsLoadedBanner(adView: AdView) {
        logE("callerAds::cAdsLoadedBanner:")
        mGoogleBannerAds?.onAdLoaded(adView)
    }

    override fun cAdsLoadedNative(nativeAd: NativeAd) {
        val adsType = this.getCurrentAdsType()
        logE("callerAds::cAdsLoadedNative:$adsType")
        if (adsType == 2) {
            mGoogleNativeAds?.showLoadedXlAd(this@CallerIdActivity, nativeAd)
        } else {
            mGoogleNativeAds?.showLoadedXxlAd(this@CallerIdActivity, nativeAd)
        }
    }

    override fun cAdsImpression() {
        logE("callerAds::cAdsImpression:")
    }

    override fun cAdsFailedToLoad() {
        mGoogleBannerAds?.onAdFailedToLoad()
        mGoogleNativeAds?.onFailedToLoadXlAd()
        mGoogleNativeAds?.onFailedToLoadXxlAd()
    }


}

