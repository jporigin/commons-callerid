package com.origin.commons.callerid.ui.fragment

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.telephony.SmsManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.origin.commons.callerid.R
import com.origin.commons.callerid.databinding.FragmentMessageBinding
import com.origin.commons.callerid.extensions.showCustomToast
import com.origin.commons.callerid.extensions.value

class MessageFragment : Fragment() {

    private val _binding by lazy {
        FragmentMessageBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        init()

        return _binding.root
    }

    private fun init() {
        clickEvents()
    }

    private fun clickEvents() {
        with(_binding) {
            llMsg1.setOnClickListener {
                tvMsg1.setTextColor(ContextCompat.getColor(requireContext(), R.color.ci_app_color))
                tvMsg2.setTextColor(ContextCompat.getColor(requireContext(), R.color.ci_txt_color))
                tvMsg3.setTextColor(ContextCompat.getColor(requireContext(), R.color.ci_txt_color))
                ivSend1.visibility = View.VISIBLE
                ivSend2.visibility = View.INVISIBLE
                ivSend3.visibility = View.INVISIBLE
                ivSend4.visibility = View.INVISIBLE
                ivRadio1.setImageResource(R.drawable.ic_selected)
                ivRadio2.setImageResource(R.drawable.ic_unselected)
                ivRadio3.setImageResource(R.drawable.ic_unselected)
                ivEdit.setImageResource(R.drawable.ic_edit_unselected)
            }
            llMsg2.setOnClickListener {
                tvMsg1.setTextColor(ContextCompat.getColor(requireContext(), R.color.ci_txt_color))
                tvMsg2.setTextColor(ContextCompat.getColor(requireContext(), R.color.ci_app_color))
                tvMsg3.setTextColor(ContextCompat.getColor(requireContext(), R.color.ci_txt_color))
                ivSend1.visibility = View.INVISIBLE
                ivSend2.visibility = View.VISIBLE
                ivSend3.visibility = View.INVISIBLE
                ivSend4.visibility = View.INVISIBLE
                ivRadio1.setImageResource(R.drawable.ic_unselected)
                ivRadio2.setImageResource(R.drawable.ic_selected)
                ivRadio3.setImageResource(R.drawable.ic_unselected)
                ivEdit.setImageResource(R.drawable.ic_edit_unselected)
            }
            llMsg3.setOnClickListener {
                tvMsg1.setTextColor(ContextCompat.getColor(requireContext(), R.color.ci_txt_color))
                tvMsg2.setTextColor(ContextCompat.getColor(requireContext(), R.color.ci_txt_color))
                tvMsg3.setTextColor(ContextCompat.getColor(requireContext(), R.color.ci_app_color))
                ivSend1.visibility = View.INVISIBLE
                ivSend2.visibility = View.INVISIBLE
                ivSend3.visibility = View.VISIBLE
                ivSend4.visibility = View.INVISIBLE
                ivRadio1.setImageResource(R.drawable.ic_unselected)
                ivRadio2.setImageResource(R.drawable.ic_unselected)
                ivRadio3.setImageResource(R.drawable.ic_selected)
                ivEdit.setImageResource(R.drawable.ic_edit_unselected)
            }
            llMsg4.setOnClickListener {
                tvMsg1.setTextColor(ContextCompat.getColor(requireContext(), R.color.ci_txt_color))
                tvMsg2.setTextColor(ContextCompat.getColor(requireContext(), R.color.ci_txt_color))
                tvMsg3.setTextColor(ContextCompat.getColor(requireContext(), R.color.ci_txt_color))
                ivSend1.visibility = View.INVISIBLE
                ivSend2.visibility = View.INVISIBLE
                ivSend3.visibility = View.INVISIBLE
                ivSend4.visibility = View.VISIBLE
                ivRadio1.setImageResource(R.drawable.ic_unselected)
                ivRadio2.setImageResource(R.drawable.ic_unselected)
                ivRadio3.setImageResource(R.drawable.ic_unselected)
                ivEdit.setImageResource(R.drawable.ic_edit)

                etMsg.postDelayed({
                    etMsg.showKeyboard()
                },50)
            }
            ivEdit.setOnClickListener {
                tvMsg1.setTextColor(ContextCompat.getColor(requireContext(), R.color.ci_txt_color))
                tvMsg2.setTextColor(ContextCompat.getColor(requireContext(), R.color.ci_txt_color))
                tvMsg3.setTextColor(ContextCompat.getColor(requireContext(), R.color.ci_txt_color))
                ivSend1.visibility = View.INVISIBLE
                ivSend2.visibility = View.INVISIBLE
                ivSend3.visibility = View.INVISIBLE
                ivSend4.visibility = View.VISIBLE
                ivRadio1.setImageResource(R.drawable.ic_unselected)
                ivRadio2.setImageResource(R.drawable.ic_unselected)
                ivRadio3.setImageResource(R.drawable.ic_unselected)
                ivEdit.setImageResource(R.drawable.ic_edit)
                etMsg.requestFocus()
//                Handler(Looper.getMainLooper()).postDelayed({
//                    if (!isKeyboardVisible(requireActivity())) {
//                        requireActivity().showKeyBoard(etMsg)
//                    }
//                }, 50)

                etMsg.postDelayed({
                    etMsg.showKeyboard()
                },50)
            }
            etMsg.setOnClickListener {
                tvMsg1.setTextColor(ContextCompat.getColor(requireContext(), R.color.ci_txt_color))
                tvMsg2.setTextColor(ContextCompat.getColor(requireContext(), R.color.ci_txt_color))
                tvMsg3.setTextColor(ContextCompat.getColor(requireContext(), R.color.ci_txt_color))
                ivSend1.visibility = View.INVISIBLE
                ivSend2.visibility = View.INVISIBLE
                ivSend3.visibility = View.INVISIBLE
                ivSend4.visibility = View.VISIBLE
                ivRadio1.setImageResource(R.drawable.ic_unselected)
                ivRadio2.setImageResource(R.drawable.ic_unselected)
                ivRadio3.setImageResource(R.drawable.ic_unselected)
                ivEdit.setImageResource(R.drawable.ic_edit)
                etMsg.requestFocus()
//                Handler(Looper.getMainLooper()).postDelayed({
//                    if (!isKeyboardVisible(requireActivity())) {
//                        requireActivity().showKeyBoard(etMsg)
//                    }
//                }, 50)

                etMsg.postDelayed({
                    etMsg.showKeyboard()
                },50)
            }
            etMsg.setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    tvMsg1.setTextColor(ContextCompat.getColor(requireContext(), R.color.ci_txt_color))
                    tvMsg2.setTextColor(ContextCompat.getColor(requireContext(), R.color.ci_txt_color))
                    tvMsg3.setTextColor(ContextCompat.getColor(requireContext(), R.color.ci_txt_color))
                    ivSend1.visibility = View.INVISIBLE
                    ivSend2.visibility = View.INVISIBLE
                    ivSend3.visibility = View.INVISIBLE
                    ivSend4.visibility = View.VISIBLE
                    ivRadio1.setImageResource(R.drawable.ic_unselected)
                    ivRadio2.setImageResource(R.drawable.ic_unselected)
                    ivRadio3.setImageResource(R.drawable.ic_unselected)
                    ivEdit.setImageResource(R.drawable.ic_edit)
                }
            }

            ivSend1.setOnClickListener {
                val message = tvMsg1.text.toString()
                openMessage(requireContext(), message)
            }

            ivSend2.setOnClickListener {
                val message = tvMsg2.text.toString()
                openMessage(requireContext(), message)
            }

            ivSend3.setOnClickListener {
                val message = tvMsg3.text.toString()
                openMessage(requireContext(), message)
            }

            ivSend4.setOnClickListener {
                if (etMsg.value.isNotEmpty()) {
                    val message = etMsg.text.toString()
                    openMessage(requireContext(), message)
                } else {
                    requireActivity().showCustomToast("Please enter message")
                }
            }

        }
    }

//    private fun openMessage(context: Context, message: String) {
//        try {
//            val intent = Intent(Intent.ACTION_VIEW)
//            intent.type = "vnd.android-dir/mms-sms"
//            intent.putExtra("sms_body", message)
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            context.startActivity(intent)
//        } catch (e: Exception) {
//            e.printStackTrace()
//            val intent2 = Intent(Intent.ACTION_MAIN)
//            intent2.addCategory(Intent.CATEGORY_APP_MESSAGING)
//            intent2.addCategory(Intent.CATEGORY_DEFAULT)
//            intent2.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//            context.startActivity(intent2)
//        }
//    }

    /*** Replace Above Function With This Function ***/
    private fun openMessage(context: Context, message: String) {
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


    private fun sendMessage(mobileNumber: String, message: String) {
//        val smsIntent = Intent(Intent.ACTION_SENDTO)
//        smsIntent.addCategory(Intent.CATEGORY_DEFAULT)
//        smsIntent.setType("vnd.android-dir/mms-sms")
//        smsIntent.putExtra("address", "882645964")
//        smsIntent.putExtra("sms_body", message)
//        startActivity(smsIntent)
        val smsManager = SmsManager.getDefault()
        smsManager.sendTextMessage(mobileNumber, null, message, null, null)
        requireActivity().showCustomToast("Message sent")
    }


}