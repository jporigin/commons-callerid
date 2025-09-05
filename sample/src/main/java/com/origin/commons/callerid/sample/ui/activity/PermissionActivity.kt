package com.origin.commons.callerid.sample.ui.activity

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.loukwn.stagestepbar.StageStepBar
import com.origin.commons.callerid.extensions.logEventE
import com.origin.commons.callerid.helpers.CallerIdUtils.isNotificationPermissionGranted
import com.origin.commons.callerid.helpers.CallerIdUtils.isPhoneStatePermissionGranted
import com.origin.commons.callerid.helpers.CallerIdUtils.isScreenOverlayEnabled
import com.origin.commons.callerid.sample.R
import com.origin.commons.callerid.sample.databinding.ActivityPermissionBinding
import com.origin.commons.callerid.sample.extensions.isNotiPermissionReqAsked
import com.origin.commons.callerid.sample.extensions.isPermissionRequestAsked
import com.origin.commons.callerid.sample.extensions.setNotiPermissionReqAsked
import com.origin.commons.callerid.sample.extensions.setPermissionGranted
import com.origin.commons.callerid.sample.extensions.setPermissionRequestAsked
import com.origin.commons.callerid.sample.extensions.setScreenOverlayEnabled
import com.origin.commons.callerid.sample.extensions.startIntentWithFlags
import com.origin.commons.callerid.sample.viewmodel.PermissionState
import com.origin.commons.callerid.sample.viewmodel.PermissionUiState
import com.origin.commons.callerid.sample.viewmodel.PermissionViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PermissionActivity : AppCompatActivity() {

    private val _binding by lazy {
        ActivityPermissionBinding.inflate(layoutInflater)
    }

    private val viewModel by lazy { PermissionViewModel() }

    private val overlayPermissionHandler = Handler(Looper.getMainLooper())
    private val overlayPermissionCheckInterval = 1000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge(SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT), SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT))
        setContentView(_binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setUpObservers()
        setUpClickEvents()
        viewModel.checkPermissions(this)
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
        logEventE("Perm_Act_onCreate")
    }

    private fun setUpObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { uiState ->
                    updateUI(uiState)
                    updatePreferenceIfNot(uiState)
                }
            }
        }
    }

    private fun setUpClickEvents() {
        _binding.btnAskPermission.setOnClickListener {
            logEventE("Perm_Act_Allow_btnClk")
            when (viewModel.uiState.value.permissionState) {
                PermissionState.REGULAR_PERMISSION -> {
                    if (!isPhoneStatePermissionGranted(this)) {
                        requestPhonePermission()
                    } else {
                        requestNotificationPermission()
                    }
                }

                PermissionState.DISPLAY_OVER_APP_PERMISSION -> {
                    requestScreenOverlayPermission()
                }

                PermissionState.COMPLETE -> {
                    proceedToMainActivity()
                }
            }
        }
    }

    private val requestOpenSetting = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (isPhoneStatePermissionGranted(this) && isNotificationPermissionGranted(this)) {
            setPermissionGranted(true)
            viewModel.updatePermissionStatus(this)
        }
    }

    private val requestPhonePermission = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        val granted = permissions.entries.all { it.value }
        if (granted) {
            logEventE("Allowed_Ph_Calls_Permission")
            if (isPhoneStatePermissionGranted(this) && isNotificationPermissionGranted(this)) {
                setPermissionGranted(true)
                viewModel.updatePermissionStatus(this)
            } else {
                if (!isPhoneStatePermissionGranted(this)) {
                    requestPhonePermission()
                } else {
                    if (!isNotificationPermissionGranted(this)) {
                        requestNotificationPermission()
                    }
                }
            }
        } else {
            if (!isNotiPermissionReqAsked()) {
                setNotiPermissionReqAsked(true)
                requiredPermissionDialog(this, R.string.required_permission, R.string.required_permission_desc, onNegativeClick = {}, onPositiveClick = {
                    if (!isPhoneStatePermissionGranted(this)) {
                        requestPhonePermission()
                    } else {
                        requestNotificationPermission()
                    }
                })
            } else {
                openSettingDialog(this, R.string.open_setting, R.string.open_setting_desc, onNegativeClick = {}, onPositiveClick = {
                    requestOpenSetting.launch(
                        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", packageName, null)
                        }
                    )
                })
            }
        }
    }

    private val requestNotificationPermission = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        val granted = permissions.entries.all { it.value }
        if (granted) {
            logEventE("Allowed_Notif_Permission")
            if (isPhoneStatePermissionGranted(this) && isNotificationPermissionGranted(this)) {
                setPermissionGranted(true)
                viewModel.updatePermissionStatus(this)
            } else {
                if (!isPhoneStatePermissionGranted(this)) {
                    requestPhonePermission()
                } else {
                    if (!isNotificationPermissionGranted(this)) {
                        requestNotificationPermission()
                    }
                }
            }
        } else {
            if (!isPermissionRequestAsked()) {
                setPermissionRequestAsked(true)
                requiredPermissionDialog(
                    this, R.string.required_permission, R.string.required_permission_desc,
                    onNegativeClick = {}, onPositiveClick = {
                        if (!isPhoneStatePermissionGranted(this)) {
                            requestPhonePermission()
                        } else {
                            requestNotificationPermission()
                        }
                    }
                )
            } else {
                openSettingDialog(
                    this, R.string.open_setting, R.string.open_setting_desc,
                    onNegativeClick = {}, onPositiveClick = {
                        requestOpenSetting.launch(
                            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                data = Uri.fromParts("package", packageName, null)
                            }
                        )
                    }
                )
            }
        }
    }

    private fun requestPhonePermission() {
        if (ContextCompat.checkSelfPermission(this@PermissionActivity, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            requestPhonePermission.launch(arrayOf(Manifest.permission.READ_PHONE_STATE))
            logEventE("Request_Ph_Calls_Permission")
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermission.launch(arrayOf(Manifest.permission.POST_NOTIFICATIONS))
            logEventE("Request_Notif_Permission")
        }
    }

    private val requestScreenOverlayPermission = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (isScreenOverlayEnabled(this)) {
            setScreenOverlayEnabled(true)
            viewModel.updatePermissionStatus(this)
        }
    }
    private val checkOverlayPermissionRunnable = object : Runnable {
        override fun run() {
            if (isScreenOverlayEnabled(this@PermissionActivity)) {
                overlayPermissionHandler.removeCallbacks(this)
                if (isPhoneStatePermissionGranted(this@PermissionActivity) && isNotificationPermissionGranted(this@PermissionActivity)) {
                    logEventE("Allowed_Sc_Overlay_Permission")
                    proceedToMainActivity()
                } else {
                    val flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    startIntentWithFlags(PermissionActivity::class.java, flags)
                }
            } else {
                overlayPermissionHandler.postDelayed(this, overlayPermissionCheckInterval)
            }
        }
    }

    private fun requestScreenOverlayPermission() {
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, "package:${packageName}".toUri())
        requestScreenOverlayPermission.launch(intent)
        overlayPermissionHandler.post(checkOverlayPermissionRunnable)
        logEventE("Request_Sc_Overlay_Permission")
    }

    private fun proceedToMainActivity() {
        val flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startIntentWithFlags(MainActivity::class.java, flags)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            this@PermissionActivity.finish()
        }
    }

    private fun updateUI(uiState: PermissionUiState) {
        _binding.stageStepBar.setCurrentState(StageStepBar.State(uiState.step - 1, 0))
        _binding.tvTitle.text = uiState.title
        _binding.tvDescription.text = uiState.description
        _binding.btnAskPermission.text = uiState.btnText
    }

    private fun updatePreferenceIfNot(uiState: PermissionUiState) {
        when (uiState.permissionState) {
            PermissionState.COMPLETE -> {
                setScreenOverlayEnabled(true)
                setPermissionGranted(true)
            }

            PermissionState.DISPLAY_OVER_APP_PERMISSION -> {
                setPermissionGranted(true)
            }

            PermissionState.REGULAR_PERMISSION -> {
                setScreenOverlayEnabled(false)
                setPermissionGranted(false)
            }
        }
    }

    private fun requiredPermissionDialog(context: Context, @StringRes title: Int, @StringRes description: Int, onNegativeClick: () -> Unit, onPositiveClick: () -> Unit) {
        val dialog = Dialog(context, R.style.CustomDialog)
        dialog.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        val layout = LayoutInflater.from(context).inflate(R.layout.dialog_confirmation, null)
        val tvTitle = layout.findViewById<TextView>(R.id.tvTitle)
        val tvDescription = layout.findViewById<TextView>(R.id.tvDescription)
        val btnCancel = layout.findViewById<TextView>(R.id.tvProcessed)
        val btnAllowPermission = layout.findViewById<TextView>(R.id.tvKeepIt)
        dialog.setContentView(layout)

        tvTitle.setText(title)
        tvDescription.setText(description)

        btnCancel.setText(R.string.ci_cancel)
        btnAllowPermission.setText(R.string.allow_permission)

        btnCancel.setOnClickListener {
            dialog.dismiss()
            onNegativeClick.invoke()
        }

        btnAllowPermission.setOnClickListener {
            onPositiveClick.invoke()
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun openSettingDialog(context: Context, @StringRes title: Int, @StringRes description: Int, onNegativeClick: () -> Unit = {}, onPositiveClick: () -> Unit) {
        val dialog = Dialog(context, R.style.CustomDialog)
        dialog.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        val layout = LayoutInflater.from(context).inflate(R.layout.dialog_confirmation, null)
        val tvTitle = layout.findViewById<TextView>(R.id.tvTitle)
        val tvDescription = layout.findViewById<TextView>(R.id.tvDescription)
        val btnCancel = layout.findViewById<TextView>(R.id.tvProcessed)
        val btnOpenSetting = layout.findViewById<TextView>(R.id.tvKeepIt)
        dialog.setContentView(layout)
        tvTitle.setText(title)
        tvDescription.setText(description)
        btnCancel.setText(R.string.ci_cancel)
        btnOpenSetting.setText(R.string.open_setting)
        btnCancel.setOnClickListener {
            dialog.dismiss()
            onNegativeClick.invoke()
        }
        btnOpenSetting.setOnClickListener {
            onPositiveClick.invoke()
            dialog.dismiss()
        }
        dialog.show()
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            logEventE("Perm_Act_onBackPress")
            finishAffinity()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        overlayPermissionHandler.removeCallbacks(checkOverlayPermissionRunnable)
    }

}