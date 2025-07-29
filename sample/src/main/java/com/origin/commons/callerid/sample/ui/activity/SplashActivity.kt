package com.origin.commons.callerid.sample.ui.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.origin.commons.callerid.helpers.Utils.isPhoneStatePermissionGranted
import com.origin.commons.callerid.helpers.Utils.isScreenOverlayEnabled
import com.origin.commons.callerid.sample.R
import com.origin.commons.callerid.sample.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {
    private val _binding by lazy {
        ActivitySplashBinding.inflate(layoutInflater)
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
    }

    private fun startRunning() {
        runOnUiThread {
            _binding.root.postDelayed({
                val mIntent = when {
                    !isPhoneStatePermissionGranted(this@SplashActivity) || !isScreenOverlayEnabled(this@SplashActivity) -> {
                        Intent(this@SplashActivity, PermissionActivity::class.java)
                    }

                    else -> {
                        Intent(this@SplashActivity, MainActivity::class.java)
                    }
                }
                if (mIsActivityRunning) {
                    this@SplashActivity.startActivity(mIntent)
                    finish()
                }
            }, 2000)
        }
    }

    private var mIsActivityRunning: Boolean = false
    override fun onResume() {
        super.onResume()
        mIsActivityRunning = true
        startRunning()
    }

    override fun onPause() {
        super.onPause()
        mIsActivityRunning = false
    }

}