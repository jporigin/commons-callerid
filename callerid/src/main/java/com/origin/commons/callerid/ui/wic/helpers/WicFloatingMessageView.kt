package com.origin.commons.callerid.ui.wic.helpers

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.origin.commons.callerid.R
import com.origin.commons.callerid.databinding.FloatingCallerMessageBinding
import com.origin.commons.callerid.extensions.hideKeyboard
import com.origin.commons.callerid.extensions.showCustomToast
import com.origin.commons.callerid.extensions.showKeyboard
import com.origin.commons.callerid.extensions.value

@SuppressLint("ViewConstructor")
class WicFloatingMessageView(
    private val context: Context,
    private val windowManager: WindowManager,
    private val layoutParams: WindowManager.LayoutParams,
    private val onDismiss: () -> Unit
) : LinearLayout(context) {

    private val _binding: FloatingCallerMessageBinding

    init {
        removeAllViews()
        _binding = FloatingCallerMessageBinding.inflate(LayoutInflater.from(context), this, true)
        setupClickListeners()
    }

    private fun setupClickListeners() {
        with(_binding) {
            val ciAppColor = ContextCompat.getColor(context, R.color.call_theme_primary)
            val ciTxtColor = ContextCompat.getColor(context, R.color.call_theme_onSurface)
            clickMsgLl1()
            _binding.ivCollapse.setOnClickListener {
                dismiss()
            }
            llMsg1.setOnClickListener {
                clickMsgLl1()
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
                etClearFocus(etMsg)
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
                etClearFocus(etMsg)
            }
            ivEdit.setOnClickListener {
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
                etClearFocus(etMsg)
                etRequestFocus(etMsg)
            }
            llMsg4.setOnClickListener {
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
                etClearFocus(etMsg)
                etRequestFocus(etMsg)
            }

            etMsg.setOnClickListener {
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
            etMsg.setOnEditorActionListener { _, actionID: Int, _ ->
                if (actionID == android.view.inputmethod.EditorInfo.IME_ACTION_DONE) {
                    clickSend4()
                    true
                }
                false
            }

            etMsg.setOnFocusChangeListener { v, hasFocus ->
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
                try {
                    if (hasFocus) {
                        // show flag
                        layoutParams.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        windowManager.updateViewLayout(this@WicFloatingMessageView, layoutParams)
                        v.showKeyboard()
                    } else {
                        // hide flag
                        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        windowManager.updateViewLayout(this@WicFloatingMessageView, layoutParams)
                        v.hideKeyboard()
                    }
                } catch (_: Exception) {
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

    fun etRequestFocus(mView: View? = null) {
        try {
            if (mView != null) {
                mView.postDelayed({
                    mView.requestFocus()
                }, 250)
            } else {
                with(_binding) {
                    etMsg.postDelayed({
                        etMsg.requestFocus()
                    }, 250)
                }
            }
        } catch (_: Exception) {
        }
    }

    fun clearAllETFocus() {
        with(_binding) {
            etClearFocus(etMsg)
        }
    }

    fun etClearFocus(mView: View? = null) {
        try {
            if (mView != null) {
                mView.postDelayed({
                    mView.clearFocus()
                }, 100)
            } else {
                with(_binding) {
                    etMsg.postDelayed({
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
                etClearFocus(etMsg)
            } else {
                context.showCustomToast("Please enter message")
            }

        }
    }

    fun clickMsgLl1() {
        val ciAppColor = ContextCompat.getColor(context, R.color.call_theme_primary)
        val ciTxtColor = ContextCompat.getColor(context, R.color.call_theme_onSurface)

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
        }
        etClearFocus()
    }

    /*** Replace Above Function With This Function ***/
    private fun openMessage(context: Context, message: String) {
        dismiss()
        val packageManager = context.packageManager
        val googleMessagesPackage = "com.google.android.apps.messaging"
        try {
            packageManager.getPackageInfo(googleMessagesPackage, PackageManager.GET_ACTIVITIES)
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("smsto:")
            intent.putExtra("sms_body", message)
            intent.setPackage(googleMessagesPackage)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: PackageManager.NameNotFoundException) {
            try {
                val defaultIntent = Intent(Intent.ACTION_VIEW)
                defaultIntent.type = "vnd.android-dir/mms-sms"
                defaultIntent.putExtra("sms_body", message)
                defaultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(defaultIntent)
            } catch (e: Exception) {
                e.printStackTrace()
                val fallbackIntent = Intent(Intent.ACTION_MAIN)
                fallbackIntent.addCategory(Intent.CATEGORY_APP_MESSAGING)
                fallbackIntent.addCategory(Intent.CATEGORY_DEFAULT)
                fallbackIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(fallbackIntent)
            }
        }
    }
}