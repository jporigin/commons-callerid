package com.origin.commons.callerid.ui.wic.helpers

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import com.origin.commons.callerid.CallerIdSDKApplication
import com.origin.commons.callerid.R
import com.origin.commons.callerid.databinding.FloatingCallerMessageBinding
import com.origin.commons.callerid.extensions.hideKeyboard
import com.origin.commons.callerid.extensions.logE
import com.origin.commons.callerid.extensions.resolveThemeColor
import com.origin.commons.callerid.extensions.showCustomToast
import com.origin.commons.callerid.extensions.showKeyboard
import com.origin.commons.callerid.extensions.value
import com.origin.commons.callerid.helpers.CallerIdSDK
import java.util.logging.Handler

@SuppressLint("ViewConstructor")
class WicFloatingMessageView(private val context: Context, private val windowManager: WindowManager, private val layoutParams: WindowManager.LayoutParams, private val onDismiss: () -> Unit) : LinearLayout(context) {

    private val _binding: FloatingCallerMessageBinding

    init {
        removeAllViews()
        _binding = FloatingCallerMessageBinding.inflate(LayoutInflater.from(context), this, true)
        setupClickListeners()
    }

    private fun setupClickListeners() {
        with(_binding) {
            val ciAppColor = context.resolveThemeColor(R.attr.callThemePrimary)
            val ciTxtColor = context.resolveThemeColor(R.attr.callThemeOnSurface)
            clickMsgLl(false)
            _binding.ivCollapse.setOnClickListener {
                dismiss()
            }
            llMsg1.setOnClickListener {
                clickMsgLl(true)
            }
            llMsg2.setOnClickListener {
                tvMsg1.setTextColor(ciTxtColor)
                tvMsg2.setTextColor(ciAppColor)
                tvMsg3.setTextColor(ciTxtColor)
                ivSend1.visibility = INVISIBLE
                ivSend2.visibility = VISIBLE
                ivSend3.visibility = INVISIBLE
                ivSend4.visibility = INVISIBLE
                ivRadio1.setImageResource(R.drawable.ci_radio_unselected)
                ivRadio2.setImageResource(R.drawable.ci_radio_selected)
                ivRadio3.setImageResource(R.drawable.ci_radio_unselected)
                etClearFocus(etMsg, 96)
            }
            llMsg3.setOnClickListener {
                tvMsg1.setTextColor(ciTxtColor)
                tvMsg2.setTextColor(ciTxtColor)
                tvMsg3.setTextColor(ciAppColor)
                ivSend1.visibility = INVISIBLE
                ivSend2.visibility = INVISIBLE
                ivSend3.visibility = VISIBLE
                ivSend4.visibility = INVISIBLE
                ivRadio1.setImageResource(R.drawable.ci_radio_unselected)
                ivRadio2.setImageResource(R.drawable.ci_radio_unselected)
                ivRadio3.setImageResource(R.drawable.ci_radio_selected)
                etClearFocus(etMsg, 95)
            }
            llMsg4.setOnClickListener {
                requestEditTextFocus()
            }
            tlMsg.setOnClickListener {
                requestEditTextFocus()
            }
            etMsg.setOnClickListener {
                requestEditTextFocus()
            }
            etMsg.setOnEditorActionListener { _, actionID: Int, _ ->
                if (actionID == android.view.inputmethod.EditorInfo.IME_ACTION_DONE) {
                    clickSend4()
                    true
                }
                false
            }

            etMsg.setOnFocusChangeListener { v, hasFocus ->
                logE("check::hasFocus: $hasFocus")
                try {
                    if (hasFocus) {
                        logE("check::showKeyboard: ")
                        // show flag
                        layoutParams.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        windowManager.updateViewLayout(this@WicFloatingMessageView, layoutParams)
                        //
                        android.os.Handler(Looper.getMainLooper()).postDelayed({
                            v.showKeyboard()
                        }, 400L)
                    } else {
                        logE("check::hideKeyboard: ")
                        // hide flag
                        layoutParams.flags =
                            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        windowManager.updateViewLayout(this@WicFloatingMessageView, layoutParams)
                        //
                        v.hideKeyboard()
                    }
                } catch (_: Exception) {
                }

                if (hasFocus) {
                    tvMsg1.setTextColor(ciTxtColor)
                    tvMsg2.setTextColor(ciTxtColor)
                    tvMsg3.setTextColor(ciTxtColor)
                    ivSend1.visibility = INVISIBLE
                    ivSend2.visibility = INVISIBLE
                    ivSend3.visibility = INVISIBLE
                    ivSend4.visibility = VISIBLE
                    ivRadio1.setImageResource(R.drawable.ci_radio_unselected)
                    ivRadio2.setImageResource(R.drawable.ci_radio_unselected)
                    ivRadio3.setImageResource(R.drawable.ci_radio_unselected)
                }

            }

            ivSend1.setOnClickListener {
                val message = tvMsg1.text.toString()
                openMessage(context, message)
            }

            ivSend2.setOnClickListener {
                val message = tvMsg2.text.toString()
                openMessage(context, message)
            }

            ivSend3.setOnClickListener {
                val message = tvMsg3.text.toString()
                openMessage(context, message)
            }

            ivSend4.setOnClickListener {
                clickSend4()
            }
        }
    }

    private fun dismiss() {
        clearAllETFocus()
        try {
            if (isAttachedToWindow) {
                windowManager.removeView(this)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        onDismiss.invoke()
    }

    fun requestEditTextFocus() {
        with(_binding) {
            val ciTxtColor = context.resolveThemeColor(R.attr.callThemeOnSurface)
            tvMsg1.setTextColor(ciTxtColor)
            tvMsg2.setTextColor(ciTxtColor)
            tvMsg3.setTextColor(ciTxtColor)
            ivSend1.visibility = INVISIBLE
            ivSend2.visibility = INVISIBLE
            ivSend3.visibility = INVISIBLE
            ivSend4.visibility = VISIBLE
            ivRadio1.setImageResource(R.drawable.ci_radio_unselected)
            ivRadio2.setImageResource(R.drawable.ci_radio_unselected)
            ivRadio3.setImageResource(R.drawable.ci_radio_unselected)
            etRequestFocus(etMsg)
        }
    }

    fun etRequestFocus(mView: View? = null, delayMillis: Long = 250) {
        try {
            if (mView != null) {
                mView.postDelayed({
                    logE("check:: requestFocus:00")
                    mView.requestFocus()
                }, delayMillis)
            } else {
                with(_binding) {
                    etMsg.postDelayed({
                        logE("check:: requestFocus:11")
                        etMsg.requestFocus()
                    }, delayMillis)
                }
            }
        } catch (_: Exception) {
        }
    }

    fun clearAllETFocus() {
        with(_binding) {
            etClearFocus(etMsg, 99)
        }
    }

    fun etClearFocus(mView: View? = null, delInt: Int) {
        try {
            if (mView != null) {
                mView.postDelayed({
                    logE("check:: clearFocus:00: $delInt")

                    mView.clearFocus()
                }, 100)
            } else {
                with(_binding) {
                    etMsg.postDelayed({
                        logE("check:: clearFocus:11: $delInt")
                        etMsg.clearFocus()
                    }, 100)
                }
            }
        } catch (_: Exception) {
        }
    }

    fun clickSend4() {
        with(_binding) {
            if (etMsg.value.isNotEmpty()) {
                val message = etMsg.text.toString()
                openMessage(context, message)
                etClearFocus(etMsg, 98)
            } else {
                context.showCustomToast("Please enter message")
            }
        }
    }

    fun clickMsgLl(isClick: Boolean) {
        val ciAppColor = context.resolveThemeColor(R.attr.callThemePrimary)
        val ciTxtColor = context.resolveThemeColor(R.attr.callThemeOnSurface)

        with(_binding) {
            tvMsg1.setTextColor(ciAppColor)
            tvMsg2.setTextColor(ciTxtColor)
            tvMsg3.setTextColor(ciTxtColor)
            ivSend1.visibility = VISIBLE
            ivSend2.visibility = INVISIBLE
            ivSend3.visibility = INVISIBLE
            ivSend4.visibility = INVISIBLE
            ivRadio1.setImageResource(R.drawable.ci_radio_selected)
            ivRadio2.setImageResource(R.drawable.ci_radio_unselected)
            ivRadio3.setImageResource(R.drawable.ci_radio_unselected)
            if (isClick) {
                etClearFocus(etMsg, 96)
            }
        }

    }

    /*** Replace Above Function With This Function ***/
    private fun openMessage(context: Context, message: String) {
        dismiss()
        val callerIdSDKApplication = try {
            this.context.applicationContext as? CallerIdSDKApplication
        } catch (_: Exception) {
            null
        }
        try {
            if (CallerIdSDK.isHostDefaultSmsApp()) {
                val openClassForDefaultApp = callerIdSDKApplication?.openClassForDefaultApp
                when {
                    openClassForDefaultApp != null -> {
                        Intent(context, openClassForDefaultApp.provide()).apply {
                            if (message.isNotEmpty()) {
                                putExtra("sms_body", message)
                                putExtra(Intent.EXTRA_TEXT, message)
                            }
                            putExtra("isFromCallerId", true)
                            context.startActivity(this)
                        }
                    }

                    else -> {
                        val intent = Intent(Intent.ACTION_MAIN).apply {
                            addCategory(Intent.CATEGORY_APP_MESSAGING)
                        }
                        if (message.isNotEmpty()) {
                            intent.putExtra("sms_body", message)
                        }
                        context.startActivity(intent)
                    }
                }
            } else {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    type = "vnd.android-dir/mms-sms"
                }
                if (message.isNotEmpty()) {
                    intent.putExtra("sms_body", message)
                }
                context.startActivity(intent)
            }
        } catch (e: ActivityNotFoundException) {
            try {
                val intent = Intent(Intent.ACTION_MAIN).apply {
                    addCategory(Intent.CATEGORY_APP_MESSAGING)
                }
                if (message.isNotEmpty()) {
                    intent.putExtra("sms_body", message)
                }
                context.startActivity(intent)
            } catch (inner: Exception) {
                inner.printStackTrace()
                // Optionally show toast or fallback
            }
        } catch (e: Exception) {
            e.printStackTrace()
            try {
                val intent = Intent(Intent.ACTION_MAIN).apply {
                    addCategory(Intent.CATEGORY_APP_MESSAGING)
                }
                if (message.isNotEmpty()) {
                    intent.putExtra("sms_body", message)
                }
                context.startActivity(intent)
            } catch (inner: Exception) {
                inner.printStackTrace()
            }
        }
    }
}