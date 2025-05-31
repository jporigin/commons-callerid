package com.origin.commons.callerid.ui.activity

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.origin.commons.callerid.R
import com.origin.commons.callerid.ads.AdFormat
import com.origin.commons.callerid.ads.bannerads.GoogleBannerAds
import com.origin.commons.callerid.ads.nativeads.GoogleNativeAds
import com.origin.commons.callerid.ads.utils.getBannerAdSize
import com.origin.commons.callerid.databinding.ActivityDetailBinding
import com.origin.commons.callerid.extensions.beGone
import com.origin.commons.callerid.extensions.beInvisible
import com.origin.commons.callerid.extensions.logEventE
import com.origin.commons.callerid.extensions.prefsHelper
import com.origin.commons.callerid.ui.fragment.HomeFragment
import com.origin.commons.callerid.ui.fragment.MessageFragment
import com.origin.commons.callerid.ui.fragment.MoreFragment
import com.origin.commons.callerid.ui.fragment.NotificationFragment

class DetailActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityDetailBinding

    private val phoneNumber by lazy { intent.getStringExtra("phoneNumber") }
    private val time by lazy { intent.getStringExtra("time") }
    private val duration by lazy { intent.getStringExtra("duration") }
    private val callType by lazy { intent.getStringExtra("callType") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.enableEdgeToEdge(SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT), SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT))
        _binding = ActivityDetailBinding.inflate(layoutInflater)
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
        this@DetailActivity.prefsHelper.apply {
            if (this.isMissedCallFeatureEnable) {
                appendValue += "_m"
            } else if (this.isCompleteCallFeatureEnable) {
                appendValue += "_c"
            } else if (this.isNoAnswerFeatureEnable) {
                appendValue += "_n"
            } else {
                ""
            }
            this@DetailActivity.logEventE("Showed_OGCallerScreen$appendValue")
        }
    }

    private fun setUpAds() {
        this@DetailActivity.prefsHelper.apply {
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
                    mGoogleNativeAds?.showXlAd(this@DetailActivity, mAdUnitId)
                }

                AdFormat.NATIVE_BIG -> {
                    _binding.incAdsView.rlMainGoogleAds.beInvisible()
                    setupGoogleNativeAds()
                    mGoogleNativeAds?.showXxlAd(this@DetailActivity, mAdUnitId)
                }

                else -> {
                    _binding.incAdsView.rlMainGoogleAds.beGone()
                }
            }
        }

    }


    private fun setUpTabPagerAdapter() {
        _binding.vpTab.apply {
            adapter = TabPagerAdapter(this@DetailActivity)
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


        _binding.ivLogo.setOnClickListener {
            var mClass1: Class<*>? = try {
                Class.forName(this@DetailActivity.prefsHelper.mClass1)
            } catch (_: Exception) {
                null
            }
            var mClass2High: Class<*>? = try {
                Class.forName(this@DetailActivity.prefsHelper.mClass2High)
            } catch (_: Exception) {
                null
            }
            val intent: Intent? = when {
                mClass1 != null && isActivityRunning(this@DetailActivity, mClass1) -> {
                    Intent(this@DetailActivity, mClass1)
                }

                mClass2High != null -> {
                    Intent(this@DetailActivity, mClass2High)
                }

                else -> null
            }
            intent?.let { startActivity(it) }
        }

        _binding.ivCall.setOnClickListener {
            makePhoneCall(this)
        }
    }


    fun isActivityRunning(context: Context, activityClass: Class<*>): Boolean {
        val activityManager = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val runningTasks = activityManager.appTasks
        for (task in runningTasks) {
            val info = task.taskInfo
            if (activityClass.canonicalName == info.baseActivity?.className || activityClass.canonicalName == info.topActivity?.className) {
                return true
            }
        }
        return false
    }


    private fun makePhoneCall(context: Context) {
        val intent = Intent(Intent.ACTION_DIAL)
        context.startActivity(intent)
        finishAndRemoveTask()
    }

    var mHomeFragment: HomeFragment? = null
    var mMessageFragment: MessageFragment? = null
    var mNotificationFragment: NotificationFragment? = null
    var mMoreFragment: MoreFragment? = null

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
                    this@DetailActivity, getCurrentAdFormat(this@DetailActivity) == AdFormat.NONE, this@DetailActivity.prefsHelper.mIsShowAdsShimmerView,
                    _binding.incAdsView.rlMainGoogleAds, _binding.incAdsView.flSpaceLayout, _binding.incAdsView.tvSpaceAds, _binding.incAdsView.flShimmerGoogleAds, _binding.incAdsView.flGoogleAds
                )
                showAd(this@DetailActivity, this@DetailActivity.prefsHelper.mAdUnitId, this@DetailActivity.getBannerAdSize(_binding.incAdsView.flGoogleAds))
            }
        }
    }

    private var mGoogleNativeAds: GoogleNativeAds? = null
    private fun setupGoogleNativeAds() {
        if (mGoogleNativeAds == null) {
            GoogleNativeAds().apply {
                mGoogleNativeAds = this
                setupAdsViews(
                    this@DetailActivity, getCurrentAdFormat(this@DetailActivity) == AdFormat.NONE, this@DetailActivity.prefsHelper.mIsShowAdsShimmerView,
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