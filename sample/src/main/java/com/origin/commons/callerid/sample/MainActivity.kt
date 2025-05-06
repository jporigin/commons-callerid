package com.origin.commons.callerid.sample

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.origin.commons.callerid.CallerIdSDKApplication
import com.origin.commons.callerid.ads.AdFormat
import com.origin.commons.callerid.sample.databinding.ActivityMainBinding
import com.origin.commons.callerid.sample.helpers.Utils.bannerAds
import com.origin.commons.callerid.sample.helpers.Utils.nativeAds

class MainActivity : AppCompatActivity() {
    private val _binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge(SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT), SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT))
        setContentView(_binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // initialize caller id sdk
        this@MainActivity.initCallerSDK()

        _binding.ivSetting.setOnClickListener {
            Intent(this@MainActivity, SettingActivity::class.java).apply {
                startActivity(this)
            }
        }
    }

    private fun Activity.initCallerSDK() {
        val mCallerIdSDKApplication: CallerIdSDKApplication? = try {
            this.application as CallerIdSDKApplication
        } catch (_: Exception) {
            null
        }
        mCallerIdSDKApplication?.initSDK(this)?.let { isInitialized ->
            if (isInitialized) {
                mCallerIdSDKApplication.initSDKAds(adFormat = AdFormat.NATIVE_BIG, adUnitId = nativeAds)
            }
        }
        mCallerIdSDKApplication?.setUpClassToOpenApp(MainActivity::class.java, SplashActivity::class.java)
    }

}


