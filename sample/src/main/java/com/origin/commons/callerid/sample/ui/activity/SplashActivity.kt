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

        runOnUiThread {
            _binding.root.postDelayed({
                when {
                    !isPhoneStatePermissionGranted(this@SplashActivity) || !isScreenOverlayEnabled(this@SplashActivity) -> {
                        startActivity(Intent(this@SplashActivity, PermissionActivity::class.java))
                    }

                    else -> {
                        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                    }
                }
                finish()
            }, 2000)
        }

    }
}