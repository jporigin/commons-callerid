package com.origin.commons.callerid.sample.ui.activity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.origin.commons.callerid.extensions.logEventE
import com.origin.commons.callerid.extensions.openCallerIDSetting
import com.origin.commons.callerid.extensions.refreshCurrentAdsType
import com.origin.commons.callerid.helpers.Utils.calculateDuration
import com.origin.commons.callerid.helpers.Utils.formatTimeToString
import com.origin.commons.callerid.sample.R
import com.origin.commons.callerid.sample.databinding.ActivityMainBinding
import com.origin.commons.callerid.ui.activity.CallerIdActivity
import java.util.Date

class MainActivity : AppCompatActivity() {
    private val _binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge(SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT), SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT))
        setContentView(_binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsetsIgnoringVisibility(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        _binding.tvMainText.setOnClickListener {
            enableMessageTesterByClick()
        }

        _binding.ivSetting.setOnClickListener {
            this@MainActivity.openCallerIDSetting()
        }
//        if (XiaomiPermissionHelper.isXiaomiDevice()) {
//            XiaomiPermissionHelper.guideUserForXiaomiOptimizations(this)
//        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                logEventE("Main_Act_onBackPress")
                finish()
            }
        })
        logEventE("Main_Act_onCreate")
    }

    override fun onPause() {
        super.onPause()
        logEventE("Main_Act_onPause")
    }
    override fun onResume() {
        super.onResume()
        logEventE("Main_Act_onResume")
    }

    private var countClickIconSplash: Int = 0
    private val MAX_CLICK_ICON_SPLASH: Int = 6
    private fun enableMessageTesterByClick() {
        countClickIconSplash++
        if (countClickIconSplash >= MAX_CLICK_ICON_SPLASH) {
            countClickIconSplash = 0

            // testing purpose only
            refreshCurrentAdsType()
            startDetailActivity(this@MainActivity, "+919876543210", Date().time, "Missed Call")
        }
    }

    private fun startDetailActivity(context: Context, phoneNumber: String?, time: Long, callType: String) {
        val intent = Intent(context.applicationContext, CallerIdActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        intent.putExtra("phoneNumber", phoneNumber)
        intent.putExtra("time", formatTimeToString(time))
        intent.putExtra("duration", calculateDuration(time, Date().time))
        intent.putExtra("callType", callType)
        context.applicationContext.startActivity(intent)
    }

}


