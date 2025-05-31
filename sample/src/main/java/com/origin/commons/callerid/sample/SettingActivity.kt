package com.origin.commons.callerid.sample

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.origin.commons.callerid.CallerIdSDKApplication
import com.origin.commons.callerid.helpers.Utils.calculateDuration
import com.origin.commons.callerid.helpers.Utils.formatTimeToString
import com.origin.commons.callerid.sample.databinding.ActivitySettingBinding
import com.origin.commons.callerid.ui.activity.DetailActivity
import java.util.Date

class SettingActivity : AppCompatActivity() {

    private val _binding by lazy {
        ActivitySettingBinding.inflate(layoutInflater)
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

        //
        val mCallerIdSDKApplication: CallerIdSDKApplication? = try {
            this.application as CallerIdSDKApplication
        } catch (_: Exception) {
            null
        }

        //
        mCallerIdSDKApplication?.let { mApplication ->
            _binding.switchMissCall.isChecked = mApplication.getMissedCallFeatureEnable() == true
            _binding.switchCompleteCall.isChecked = mApplication.getCompleteCallFeatureEnable() == true
            _binding.switchNoAnswer.isChecked = mApplication.getNoAnswerFeatureEnable() == true
        }

        //
        _binding.switchMissCall.setOnCheckedChangeListener { _, isChecked ->
            mCallerIdSDKApplication?.let { mApplication ->
                if (isChecked) {
                    mApplication.setMissedCallFeatureEnable(true)
                } else {
                    processedDialog(
                        this, R.string.missed_call, R.string.proceed_dialog_description, onPositiveClick = {
                            confirmationDialog(
                                this,
                                R.string.confirm_dialog_title,
                                R.string.confirm_dialog_description,
                                onPositiveClick = {
                                    _binding.switchMissCall.isChecked = false
                                    mApplication.setMissedCallFeatureEnable(false)
                                },
                                onNegativeClick = {
                                    _binding.switchMissCall.isChecked = true
                                    mApplication.setMissedCallFeatureEnable(true)
                                }
                            )
                        },
                        onNegativeClick = {
                            _binding.switchMissCall.isChecked = true
                            mApplication.setMissedCallFeatureEnable(true)
                        }
                    )
                }
            }
        }
        _binding.switchCompleteCall.setOnCheckedChangeListener { _, isChecked ->
            mCallerIdSDKApplication?.let { mApplication ->
                if (isChecked) {
                    mApplication.setCompleteCallFeatureEnable(true)
                } else {
                    processedDialog(
                        this, R.string.missed_call, R.string.proceed_dialog_description, onPositiveClick = {
                            confirmationDialog(
                                this,
                                R.string.confirm_dialog_title,
                                R.string.confirm_dialog_description,
                                onPositiveClick = {
                                    _binding.switchCompleteCall.isChecked = false
                                    mApplication.setCompleteCallFeatureEnable(false)
                                },
                                onNegativeClick = {
                                    _binding.switchCompleteCall.isChecked = true
                                    mApplication.setCompleteCallFeatureEnable(true)
                                }
                            )
                        },
                        onNegativeClick = {
                            _binding.switchCompleteCall.isChecked = true
                            mApplication.setCompleteCallFeatureEnable(true)
                        }
                    )
                }
            }
        }
        _binding.switchNoAnswer.setOnCheckedChangeListener { _, isChecked ->
            mCallerIdSDKApplication?.let { mApplication ->
                if (isChecked) {
                    mApplication.setNoAnswerFeatureEnable(true)
                } else {
                    processedDialog(
                        this, R.string.missed_call, R.string.proceed_dialog_description, onPositiveClick = {
                            confirmationDialog(
                                this,
                                R.string.confirm_dialog_title,
                                R.string.confirm_dialog_description,
                                onPositiveClick = {
                                    _binding.switchNoAnswer.isChecked = false
                                    mApplication.setNoAnswerFeatureEnable(false)
                                },
                                onNegativeClick = {
                                    _binding.switchNoAnswer.isChecked = true
                                    mApplication.setNoAnswerFeatureEnable(true)
                                }
                            )
                        },
                        onNegativeClick = {
                            _binding.switchNoAnswer.isChecked = true
                            mApplication.setNoAnswerFeatureEnable(true)
                        }
                    )
                }
            }
        }
        //
        _binding.clToolbar.setOnClickListener {
            // testing purpose only
            startDetailActivity(this@SettingActivity, "+919876543210", Date().time, "Missed Call")
        }
    }

    private fun startDetailActivity(context: Context, phoneNumber: String?, time: Long, callType: String) {
        val intent = Intent(context.applicationContext, DetailActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        intent.putExtra("phoneNumber", phoneNumber)
        intent.putExtra("time", formatTimeToString(time))
        intent.putExtra("duration", calculateDuration(time, Date().time))
        intent.putExtra("callType", callType)
        context.applicationContext.startActivity(intent)
    }

    private fun processedDialog(context: Context, @StringRes title: Int, @StringRes description: Int, onPositiveClick: () -> Unit, onNegativeClick: () -> Unit) {
        val dialog = Dialog(context, R.style.CustomDialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        val layout = LayoutInflater.from(context).inflate(R.layout.dialog_confirmation, null)
        val tvTitle = layout.findViewById<TextView>(R.id.tvTitle)
        val tvDescription = layout.findViewById<TextView>(R.id.tvDescription)
        val btnProcessed = layout.findViewById<TextView>(R.id.tvProcessed)
        val btnKeepIt = layout.findViewById<TextView>(R.id.tvKeepIt)
        dialog.setContentView(layout)

        tvTitle.setText(title)
        tvDescription.setText(description)

        btnProcessed.setOnClickListener {
            dialog.dismiss()
            onPositiveClick.invoke()
        }

        btnKeepIt.setOnClickListener {
            dialog.dismiss()
            onNegativeClick.invoke()
        }
        dialog.show()
    }

    private fun confirmationDialog(context: Context, title: Int, description: Int, onPositiveClick: () -> Unit, onNegativeClick: () -> Unit) {
        val dialog = Dialog(context, R.style.CustomDialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        val layout = LayoutInflater.from(context).inflate(R.layout.dialog_confirmation, null)
        val tvTitle = layout.findViewById<TextView>(R.id.tvTitle)
        val tvDescription = layout.findViewById<TextView>(R.id.tvDescription)
        val btnProcessed = layout.findViewById<TextView>(R.id.tvProcessed)
        val btnKeepIt = layout.findViewById<TextView>(R.id.tvKeepIt)
        dialog.setContentView(layout)

        tvTitle.setText(title)
        tvDescription.setText(description)

        btnProcessed.setOnClickListener {
            dialog.dismiss()
            onPositiveClick.invoke()
        }

        btnKeepIt.setOnClickListener {
            dialog.dismiss()
            onNegativeClick.invoke()
        }
        dialog.show()
    }

}