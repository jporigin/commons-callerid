package com.origin.commons.callerid.ui.activity

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.origin.commons.callerid.CallerIdSDKApplication
import com.origin.commons.callerid.R
import com.origin.commons.callerid.databinding.ActivityCallerSettingBinding
import com.origin.commons.callerid.extensions.dpToPx
import com.origin.commons.callerid.extensions.prefsHelper
import com.origin.commons.callerid.helpers.DARK_THEME
import com.origin.commons.callerid.helpers.LIGHT_THEME
import com.origin.commons.callerid.helpers.SYSTEM_THEME
import com.origin.commons.callerid.ui.dialog.ConfirmationDialog

class CallerSettingActivity : CallerBaseActivity() {
    private val _binding by lazy {
        ActivityCallerSettingBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge(SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT), SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT))
        if (Build.VERSION.SDK_INT >= 29) {
            window.isNavigationBarContrastEnforced = false
        }
        setContentView(_binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(_binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 5.dpToPx)
            insets
        }
        _binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        setUpThemes()
        setUpMissCalls()
        setUpCompCalls()
        setUpUnAnsCall()
    }

    private fun refreshThemeSwitches() = with(_binding) {
        when (prefsHelper.selectedAppTheme) {
            SYSTEM_THEME -> {
                sSystemMode.isChecked = true
                sForceDark.apply {
                    isChecked = false
                    isEnabled = false
                }
            }

            DARK_THEME -> {
                sForceDark.isChecked = true
                sSystemMode.apply {
                    isChecked = false
                    isEnabled = false
                }
            }

            else -> {
                sSystemMode.apply {
                    isChecked = false
                    isEnabled = true
                }
                sForceDark.apply {
                    isChecked = false
                    isEnabled = true
                }
            }
        }
    }


    private fun setUpThemes() {
        refreshThemeSwitches()
        _binding.clSystemMode.setOnClickListener {
            val newTheme = if (_binding.sSystemMode.isChecked) {
                LIGHT_THEME
            } else {
                SYSTEM_THEME
            }
            this@CallerSettingActivity.prefsHelper.selectedAppTheme = newTheme
            refreshThemeSwitches()
            refreshTheme("clSystemMode", true)
        }

        _binding.clForceDark.setOnClickListener {
            val newTheme = if (prefsHelper.selectedAppTheme == DARK_THEME) {
                LIGHT_THEME
            } else {
                DARK_THEME
            }
            this@CallerSettingActivity.prefsHelper.selectedAppTheme = newTheme
            refreshThemeSwitches()
            refreshTheme("clForceDark", true)
        }
    }


    private fun setUpMissCalls() {
        val mCallerIdSDKApplication: CallerIdSDKApplication? = try {
            this.application as CallerIdSDKApplication
        } catch (_: Exception) {
            null
        }
        mCallerIdSDKApplication?.let { mApplication ->
            val mIsChecked = mApplication.getMissedCallFeatureEnable()
            _binding.switchMissCall.isChecked = mIsChecked
        }
        _binding.clMissCall.setOnClickListener {
            if (!_binding.switchMissCall.isChecked) {
                toggleMiscalls(mCallerIdSDKApplication)
            } else {
                runOnUiThread {
                    if (!isDestroyed && !isFinishing) {
                        val msgBuilder =
                            StringBuilder().append("• ").append(String.format(this.getString(R.string.caller_toggle_agree_title_2), getString(R.string.missed_call))).append("\n").append("\n")
                                .append("• ").append(this.getString(R.string.caller_toggle_agree_message_1))
                        ConfirmationDialog(
                            activity = this,
                            titleId = R.string.caller_toggle_agree_title_1,
                            message = msgBuilder.toString(),
                            positive = R.string.keep_it,
                            negative = R.string.proceed,
                            cancelOnTouchOutside = false
                        ) { success ->
                            if (!success) {
                                toggleMiscalls(mCallerIdSDKApplication)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun toggleMiscalls(mCallerIdSDKApplication: CallerIdSDKApplication?) {
        mCallerIdSDKApplication?.let { mApplication ->
            val mIsChecked = mApplication.getMissedCallFeatureEnable()
            mApplication.setMissedCallFeatureEnable(!mIsChecked)
            _binding.switchMissCall.isChecked = !mIsChecked
        }
    }

    private fun setUpCompCalls() {
        val mCallerIdSDKApplication: CallerIdSDKApplication? = try {
            this.application as CallerIdSDKApplication
        } catch (_: Exception) {
            null
        }
        mCallerIdSDKApplication?.let { mApplication ->
            val mIsChecked = mApplication.getCompleteCallFeatureEnable()
            _binding.switchCompleteCall.isChecked = mIsChecked
        }
        _binding.clCompleteCall.setOnClickListener {
            if (!_binding.switchCompleteCall.isChecked) {
                toggleCompCalls(mCallerIdSDKApplication)
            } else {
                runOnUiThread {
                    if (!isDestroyed && !isFinishing) {
                        val msgBuilder = StringBuilder()
                            .append("• ")
                            .append(String.format(this.getString(R.string.caller_toggle_agree_title_2), getString(R.string.complete_call)))
                            .append("\n")
                            .append("\n")
                            .append("• ")
                            .append(this.getString(R.string.caller_toggle_agree_message_1))

                        ConfirmationDialog(
                            activity = this, titleId = R.string.caller_toggle_agree_title_1, message = msgBuilder.toString(), positive = R.string.keep_it,
                            negative = R.string.proceed, cancelOnTouchOutside = false
                        ) { success ->

                            if (!success) {
                                toggleCompCalls(mCallerIdSDKApplication)
                            }

                        }
                    }
                }
            }
        }
    }

    private fun toggleCompCalls(mCallerIdSDKApplication: CallerIdSDKApplication?) {
        mCallerIdSDKApplication?.let { mApplication ->
            val mIsChecked = mApplication.getCompleteCallFeatureEnable()
            mApplication.setCompleteCallFeatureEnable(!mIsChecked)
            _binding.switchCompleteCall.isChecked = !mIsChecked
        }
    }


    private fun setUpUnAnsCall() {
        val mCallerIdSDKApplication: CallerIdSDKApplication? = try {
            this.application as CallerIdSDKApplication
        } catch (_: Exception) {
            null
        }
        mCallerIdSDKApplication?.let { mApplication ->
            val mIsChecked = mApplication.getNoAnswerFeatureEnable()
            _binding.switchNoAnswer.isChecked = mIsChecked
        }
        _binding.clNoAnswer.setOnClickListener {
            if (!_binding.switchNoAnswer.isChecked) {
                toggleUnAnsCall(mCallerIdSDKApplication)
            } else {
                runOnUiThread {
                    if (!isDestroyed && !isFinishing) {
                        val msgBuilder =
                            StringBuilder().append("• ").append(String.format(this.getString(R.string.caller_toggle_agree_title_2), getString(R.string.no_answer))).append("\n").append("\n")
                                .append("• ")
                                .append(this.getString(R.string.caller_toggle_agree_message_1))
                        ConfirmationDialog(
                            activity = this, titleId = R.string.caller_toggle_agree_title_1, message = msgBuilder.toString(), positive = R.string.keep_it,
                            negative = R.string.proceed, cancelOnTouchOutside = false
                        ) { success ->
                            if (!success) {
                                toggleUnAnsCall(mCallerIdSDKApplication)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun toggleUnAnsCall(mCallerIdSDKApplication: CallerIdSDKApplication?) {
        mCallerIdSDKApplication?.let { mApplication ->
            val mIsChecked = mApplication.getNoAnswerFeatureEnable()
            mApplication.setNoAnswerFeatureEnable(!mIsChecked)
            _binding.switchNoAnswer.isChecked = !mIsChecked
        }
    }
}