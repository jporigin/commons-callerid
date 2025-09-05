package com.origin.commons.callerid.ui.activity

import android.os.Build
import android.os.Bundle
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.origin.commons.callerid.R
import com.origin.commons.callerid.databinding.ActivityCallerSettingBinding
import com.origin.commons.callerid.extensions.prefsHelper
import com.origin.commons.callerid.helpers.HomeKeyWatcher
import com.origin.commons.callerid.model.PopViewType
import com.origin.commons.callerid.model.ThemeConfig
import com.origin.commons.callerid.ui.dialog.ChoosePopupDialog
import com.origin.commons.callerid.ui.dialog.ConfirmationDialog
import kotlinx.coroutines.launch

class CallerSettingActivity : CallerBaseActivity(), HomeKeyWatcher.OnHomeAndRecentsListener {
    private val _binding by lazy {
        ActivityCallerSettingBinding.inflate(layoutInflater)
    }
    var watcher: HomeKeyWatcher? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= 29) {
            window.isNavigationBarContrastEnforced = false
        }
        setContentView(_binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(_binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        _binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        if (watcher == null) {
            watcher = HomeKeyWatcher(this@CallerSettingActivity).apply {
                setListener(this@CallerSettingActivity)
                startWatching()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setUpThemes()
        setUpAppearanceVisibility()
        setUpPopupSettings()
        setUpMissCalls()
        setUpCompCalls()
        setUpUnAnsCall()
        setupStandardPopup()
    }

    override fun onDestroy() {
        watcher?.stopWatching()
        watcher = null
        super.onDestroy()
    }

    override fun onHomePressed() {
        onBackPressedDispatcher.onBackPressed()
    }

    override fun onRecentsPressed() {
        onBackPressedDispatcher.onBackPressed()
    }

    private fun refreshThemeSwitches() = with(_binding) {
        when (prefsHelper.themeConfig) {
            ThemeConfig.SYSTEM_THEME -> {
                sSystemMode.isChecked = true
                sForceDark.apply {
                    isChecked = false
                    isEnabled = false
                }
            }

            ThemeConfig.LIGHT_THEME -> {
                sSystemMode.apply {
                    isChecked = false
                    isEnabled = true
                }
                sForceDark.apply {
                    isChecked = false
                    isEnabled = true
                }
            }

            ThemeConfig.DARK_THEME -> {
                sForceDark.isChecked = true
                sSystemMode.apply {
                    isChecked = false
                    isEnabled = false
                }
            }

        }
    }

    private fun setUpThemes() {
        refreshThemeSwitches()
        _binding.clSystemMode.setOnClickListener {
            val newTheme = if (_binding.sSystemMode.isChecked) {
                ThemeConfig.LIGHT_THEME
            } else {
                ThemeConfig.SYSTEM_THEME
            }
            this@CallerSettingActivity.prefsHelper.themeConfig = newTheme
            refreshThemeSwitches()
            refreshTheme("clSystemMode", true)
        }

        _binding.clForceDark.setOnClickListener {
            val newTheme = if (prefsHelper.themeConfig == ThemeConfig.DARK_THEME) {
                ThemeConfig.LIGHT_THEME
            } else {
                ThemeConfig.DARK_THEME
            }
            this@CallerSettingActivity.prefsHelper.themeConfig = newTheme
            refreshThemeSwitches()
            refreshTheme("clForceDark", true)
        }
    }

    private fun setUpAppearanceVisibility() {
        prefsHelper.showThemeSettings.let { isVisible ->
            _binding.tvAppearance.isVisible = isVisible
            _binding.clSystemMode.isVisible = isVisible
            _binding.clForceDark.isVisible = isVisible
        }
    }

    private fun setUpPopupSettings() {
        prefsHelper.showOnlyCallerIdScreen.let { show ->
            _binding.tvGeneral.isVisible = !show
            _binding.clPopupSetting.isVisible = !show
            _binding.clStandardViewOption.isVisible = !show && prefsHelper.popViewType == PopViewType.CLASSIC
        }
        _binding.clStandardViewOption.isVisible = prefsHelper.popViewType == PopViewType.CLASSIC
        val title = when(prefsHelper.popViewType) {
            PopViewType.CLASSIC -> "Classic"
            PopViewType.STANDARD -> "Standard"
        }
        _binding.tvPopupDescription.text = title
        lifecycleScope.launch {
            prefsHelper.keyChangesFlow(lifecycleScope).collect { changedKey ->
                if (changedKey == "popViewType") {
                    val newTitle = when(prefsHelper.popViewType) {
                        PopViewType.CLASSIC -> "Classic"
                        PopViewType.STANDARD -> "Standard"
                    }
                    _binding.tvPopupDescription.text = newTitle
                    _binding.clStandardViewOption.isVisible = prefsHelper.popViewType == PopViewType.CLASSIC && !prefsHelper.showOnlyCallerIdScreen
                }
            }
        }

        _binding.clPopupSetting.setOnClickListener {
            ChoosePopupDialog(this)
        }
    }

    private fun setUpMissCalls() {
        _binding.switchMissCall.isChecked = prefsHelper.isMissedCallFeatureEnable
        _binding.clMissCall.setOnClickListener {
            if (!_binding.switchMissCall.isChecked) {
                toggleMiscalls()
            } else {
                runOnUiThread {
                    if (!isDestroyed && !isFinishing) {
                        val msgBuilder =
                            StringBuilder().append("• ").append(
                                String.format(
                                    this.getString(R.string.ci_caller_toggle_agree_title_2),
                                    getString(R.string.ci_missed_call)
                                )
                            ).append("\n").append("\n")
                                .append("• ")
                                .append(this.getString(R.string.ci_caller_toggle_agree_message_1))
                        ConfirmationDialog(
                            activity = this,
                            titleId = R.string.ci_caller_toggle_agree_title_1,
                            message = msgBuilder.toString(),
                            positive = R.string.ci_keep_it,
                            negative = R.string.ci_proceed,
                            cancelOnTouchOutside = false
                        ) { success ->
                            if (!success) {
                                toggleMiscalls()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun toggleMiscalls() {
        val mIsChecked = prefsHelper.isMissedCallFeatureEnable
        prefsHelper.isMissedCallFeatureEnable = !mIsChecked
        _binding.switchMissCall.isChecked = !mIsChecked
    }

    private fun setUpCompCalls() {
        _binding.switchCompleteCall.isChecked = prefsHelper.isCompleteCallFeatureEnable
        _binding.clCompleteCall.setOnClickListener {
            if (!_binding.switchCompleteCall.isChecked) {
                toggleCompCalls()
            } else {
                runOnUiThread {
                    if (!isDestroyed && !isFinishing) {
                        val msgBuilder = StringBuilder()
                            .append("• ")
                            .append(String.format(this.getString(R.string.ci_caller_toggle_agree_title_2), getString(R.string.ci_complete_call)))
                            .append("\n")
                            .append("\n")
                            .append("• ")
                            .append(this.getString(R.string.ci_caller_toggle_agree_message_1))

                        ConfirmationDialog(
                            activity = this,
                            titleId = R.string.ci_caller_toggle_agree_title_1,
                            message = msgBuilder.toString(),
                            positive = R.string.ci_keep_it,
                            negative = R.string.ci_proceed,
                            cancelOnTouchOutside = false
                        ) { success ->
                            if (!success) {
                                toggleCompCalls()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun toggleCompCalls() {
        val mIsChecked = prefsHelper.isCompleteCallFeatureEnable
        prefsHelper.isCompleteCallFeatureEnable = !mIsChecked
        _binding.switchCompleteCall.isChecked = !mIsChecked
    }

    private fun setUpUnAnsCall() {
        _binding.switchNoAnswer.isChecked = prefsHelper.isNoAnswerFeatureEnable
        _binding.clNoAnswer.setOnClickListener {
            if (!_binding.switchNoAnswer.isChecked) {
                toggleUnAnsCall()
            } else {
                runOnUiThread {
                    if (!isDestroyed && !isFinishing) {
                        val msgBuilder =
                            StringBuilder().append("• ").append(
                                String.format(
                                    this.getString(R.string.ci_caller_toggle_agree_title_2),
                                    getString(R.string.ci_no_answer)
                                )
                            ).append("\n").append("\n")
                                .append("• ")
                                .append(this.getString(R.string.ci_caller_toggle_agree_message_1))
                        ConfirmationDialog(
                            activity = this,
                            titleId = R.string.ci_caller_toggle_agree_title_1,
                            message = msgBuilder.toString(),
                            positive = R.string.ci_keep_it,
                            negative = R.string.ci_proceed,
                            cancelOnTouchOutside = false
                        ) { success ->
                            if (!success) {
                                toggleUnAnsCall()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun toggleUnAnsCall() {
        val mIsChecked = prefsHelper.isNoAnswerFeatureEnable
        prefsHelper.isNoAnswerFeatureEnable = !mIsChecked
        _binding.switchNoAnswer.isChecked = !mIsChecked
    }

    private fun setupStandardPopup() {
        _binding.sStandardView.isChecked = prefsHelper.showStandardPopupOnClassicClose
        _binding.clStandardViewOption.setOnClickListener {
            toggleShowStandardPopupOnClassicClose()
        }
    }

    private fun toggleShowStandardPopupOnClassicClose() {
        val mIsChecked = prefsHelper.showStandardPopupOnClassicClose
        prefsHelper.showStandardPopupOnClassicClose = !mIsChecked
        _binding.sStandardView.isChecked = !mIsChecked
    }
}