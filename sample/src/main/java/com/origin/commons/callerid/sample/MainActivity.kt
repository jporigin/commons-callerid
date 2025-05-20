package com.origin.commons.callerid.sample

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.origin.commons.callerid.CallerIdSDKApplication
import com.origin.commons.callerid.ads.AdFormat
import com.origin.commons.callerid.sample.databinding.ActivityMainBinding
import com.origin.commons.callerid.sample.databinding.ItemCustomViewBinding
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

    // CallerID
    private fun Activity.initCallerSDK() {
        (application as? CallerIdSDKApplication)?.let { callerIdSDKApplication ->
            val isInitialized = callerIdSDKApplication.initSDK(this)
            if (isInitialized) {
                callerIdSDKApplication.initSDKAds(adFormat = AdFormat.NATIVE_BIG, adUnitId = nativeAds)
            }
            callerIdSDKApplication.setUpClassToOpenApp(MainActivity::class.java, SplashActivity::class.java)
            callerIdSDKApplication.setUpCustomView()
        }
    }

    private fun CallerIdSDKApplication.setUpCustomView() {
        val customViewBinding = ItemCustomViewBinding.inflate(layoutInflater)
        val viewToastMap =
            mapOf(customViewBinding.v1 to "View 1", customViewBinding.v2 to "View 2", customViewBinding.v3 to "View 3", customViewBinding.v4 to "View 4", customViewBinding.v5 to "View 5")
        viewToastMap.forEach { (view, message) ->
            view.setOnClickListener {
                Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
            }
        }
        this.mCustomView = customViewBinding.root
    }
}


