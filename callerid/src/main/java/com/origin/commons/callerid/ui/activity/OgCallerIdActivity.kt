package com.origin.commons.callerid.ui.activity

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.origin.commons.callerid.R
import com.origin.commons.callerid.ads.AdFormat
import com.origin.commons.callerid.ads.bannerads.GoogleBannerAds
import com.origin.commons.callerid.ads.nativeads.GoogleNativeAds
import com.origin.commons.callerid.ads.utils.getBannerAdSize
import com.origin.commons.callerid.databinding.ActivityOgCallerIdBinding
import com.origin.commons.callerid.extensions.beGone
import com.origin.commons.callerid.extensions.beInvisible
import com.origin.commons.callerid.extensions.getOpenAppIntent
import com.origin.commons.callerid.extensions.logEventE
import com.origin.commons.callerid.extensions.prefsHelper
import com.origin.commons.callerid.ui.fragment.HomeFragment
import com.origin.commons.callerid.ui.fragment.MessageFragment
import com.origin.commons.callerid.ui.fragment.MoreFragment
import com.origin.commons.callerid.ui.fragment.NotificationFragment


class OgCallerIdActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityOgCallerIdBinding

    private val phoneNumber by lazy { intent.getStringExtra("phoneNumber") }
    private val time by lazy { intent.getStringExtra("time") }
    private val duration by lazy { intent.getStringExtra("duration") }
    private val callType by lazy { intent.getStringExtra("callType") }


    override fun getTheme(): Resources.Theme {
        val mTheme = super.getTheme()
        mTheme.applyStyle(R.style.CiTheme_Light, true)
        return mTheme
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge(SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT), SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT))
        _binding = ActivityOgCallerIdBinding.inflate(layoutInflater)
        setContentView(_binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
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
            this.packageManager.getApplicationIcon(this.packageName).let {
                _binding.ivLogo.setImageDrawable(it)
            }
        } catch (_: Exception) {
        }

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
        var appendValue = ""
        this@OgCallerIdActivity.prefsHelper.apply {
            if (this.isMissedCallFeatureEnable) {
                appendValue += "_m"
            } else if (this.isCompleteCallFeatureEnable) {
                appendValue += "_c"
            } else if (this.isNoAnswerFeatureEnable) {
                appendValue += "_n"
            } else {
                ""
            }
            this@OgCallerIdActivity.logEventE("Showed_OGCallerScreen$appendValue")
        }
    }

    override fun onDestroy() {
        mHomeFragment = null
        mMessageFragment = null
        mNotificationFragment = null
        mMoreFragment = null
        super.onDestroy()
    }

    private fun setUpAds() {
        this@OgCallerIdActivity.prefsHelper.apply {
            val adFormat: AdFormat = try {
                if (mAdFormat.isNotEmpty()) {
                    AdFormat.valueOf(mAdFormat)
                } else {
                    AdFormat.BANNER
                }
            } catch (_: Exception) {
                AdFormat.BANNER
            }
            when (adFormat) {
                AdFormat.BANNER -> {
                    _binding.incAdsView.rlMainGoogleAds.beInvisible()
                    setupGoogleBannerAds()
                }

                AdFormat.NATIVE_SMALL -> {
                    _binding.incAdsView.rlMainGoogleAds.beInvisible()
                    setupGoogleNativeAds()
                    mGoogleNativeAds?.showXlAd(this@OgCallerIdActivity, mAdUnitId)
                }

                AdFormat.NATIVE_BIG -> {
                    _binding.incAdsView.rlMainGoogleAds.beInvisible()
                    setupGoogleNativeAds()
                    mGoogleNativeAds?.showXxlAd(this@OgCallerIdActivity, mAdUnitId)
                }

                else -> {
                    _binding.incAdsView.rlMainGoogleAds.beGone()
                }
            }
        }
    }


    private fun setUpTabPagerAdapter() {
        _binding.vpTab.apply {
            this.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    var view = currentFocus
                    if (view == null) {
                        view = View(this@OgCallerIdActivity)
                    }
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                }
            })
            adapter = TabPagerAdapter(this@OgCallerIdActivity)
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
            this@OgCallerIdActivity.getOpenAppIntent()?.let { intent ->
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                this@OgCallerIdActivity.startActivity(intent)
                this@OgCallerIdActivity.finish()
            }
        }

        _binding.llCall.setOnClickListener {
            makePhoneCall(this)
        }
    }


    private fun makePhoneCall(context: Context) {
        val intent = Intent(Intent.ACTION_DIAL)
        context.startActivity(intent)
        finishAndRemoveTask()
    }

    private var mHomeFragment: HomeFragment? = null
    private var mMessageFragment: MessageFragment? = null
    private var mNotificationFragment: NotificationFragment? = null
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
                    mNotificationFragment ?: NotificationFragment().apply { mNotificationFragment = this }
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

    private fun getCurrentAdFormat(context: Context): AdFormat {
        return try {
            if (context.prefsHelper.mAdFormat.isNotEmpty()) {
                AdFormat.valueOf(context.prefsHelper.mAdFormat)
            } else {
                AdFormat.BANNER
            }
        } catch (_: Exception) {
            AdFormat.BANNER
        }
    }

    private var mGoogleBannerAds: GoogleBannerAds? = null
    private fun setupGoogleBannerAds() {
        if (mGoogleBannerAds == null) {
            GoogleBannerAds().apply {
                mGoogleBannerAds = this
                setupAdsViews(
                    this@OgCallerIdActivity, getCurrentAdFormat(this@OgCallerIdActivity) == AdFormat.NONE, this@OgCallerIdActivity.prefsHelper.mIsShowAdsShimmerView,
                    _binding.incAdsView.rlMainGoogleAds, _binding.incAdsView.flSpaceLayout, _binding.incAdsView.tvSpaceAds, _binding.incAdsView.flShimmerGoogleAds, _binding.incAdsView.flGoogleAds
                )
                showAd(this@OgCallerIdActivity, this@OgCallerIdActivity.prefsHelper.mAdUnitId, this@OgCallerIdActivity.getBannerAdSize(_binding.incAdsView.flGoogleAds))
            }
        }
    }

    private var mGoogleNativeAds: GoogleNativeAds? = null
    private fun setupGoogleNativeAds() {
        if (mGoogleNativeAds == null) {
            GoogleNativeAds().apply {
                mGoogleNativeAds = this
                setupAdsViews(
                    this@OgCallerIdActivity, getCurrentAdFormat(this@OgCallerIdActivity) == AdFormat.NONE, this@OgCallerIdActivity.prefsHelper.mIsShowAdsShimmerView,
                    _binding.incAdsView.rlMainGoogleAds, _binding.incAdsView.flSpaceLayout,
                    _binding.incAdsView.spMain, _binding.incAdsView.tvSpaceAds,
                    _binding.incAdsView.flShimmerGoogleAds, _binding.incAdsView.flGoogleAds
                )
            }
        }
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            finishAndRemoveTask()
        }
    }
}

