package com.origin.commons.callerid.ui.fragment

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import com.origin.commons.callerid.R
import com.origin.commons.callerid.databinding.FragmentMessageBinding
import com.origin.commons.callerid.extensions.getColorFromAttr
import com.origin.commons.callerid.extensions.hideKeyboard
import com.origin.commons.callerid.extensions.showCustomToast
import com.origin.commons.callerid.extensions.showKeyboard
import com.origin.commons.callerid.extensions.value


class MessageFragment : Fragment() {

    private val _binding by lazy {
        FragmentMessageBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        try {
            inflater.context.setTheme(R.style.CiTheme_Light)
        } catch (_: Exception) {
        }
        init()
        return _binding.root
    }

    override fun onPause() {
        super.onPause()
        etClearFocus()
    }

    private fun init() {
        clickEvents()
    }

    private fun clickEvents() {
        with(_binding) {
            val ciAppColor = requireContext().getColorFromAttr(R.attr.ciAppColor, R.color.ci_app_color)
            val ciTxtColor = requireContext().getColorFromAttr(R.attr.ciTxtColor, R.color.ci_txt_color)
            clickMsgLl1()
            llMsg1.setOnClickListener {
                clickMsgLl1()
            }
            llMsg2.setOnClickListener {
                tvMsg1.setTextColor(ciTxtColor)
                tvMsg2.setTextColor(ciAppColor)
                tvMsg3.setTextColor(ciTxtColor)
                ivSend1.visibility = View.INVISIBLE
                ivSend2.visibility = View.VISIBLE
                ivSend3.visibility = View.INVISIBLE
                ivSend4.visibility = View.INVISIBLE
                ivRadio1.setImageResource(R.drawable.ic_unselected)
                ivRadio2.setImageResource(R.drawable.ic_selected)
                ivRadio3.setImageResource(R.drawable.ic_unselected)
                ivEdit.setImageResource(R.drawable.ic_edit_unselected)
                etClearFocus(etMsg)
            }
            llMsg3.setOnClickListener {
                tvMsg1.setTextColor(ciTxtColor)
                tvMsg2.setTextColor(ciTxtColor)
                tvMsg3.setTextColor(ciAppColor)
                ivSend1.visibility = View.INVISIBLE
                ivSend2.visibility = View.INVISIBLE
                ivSend3.visibility = View.VISIBLE
                ivSend4.visibility = View.INVISIBLE
                ivRadio1.setImageResource(R.drawable.ic_unselected)
                ivRadio2.setImageResource(R.drawable.ic_unselected)
                ivRadio3.setImageResource(R.drawable.ic_selected)
                ivEdit.setImageResource(R.drawable.ic_edit_unselected)
                etClearFocus(etMsg)
            }
            llMsg4.setOnClickListener {
                tvMsg1.setTextColor(ciTxtColor)
                tvMsg2.setTextColor(ciTxtColor)
                tvMsg3.setTextColor(ciTxtColor)
                ivSend1.visibility = View.INVISIBLE
                ivSend2.visibility = View.INVISIBLE
                ivSend3.visibility = View.INVISIBLE
                ivSend4.visibility = View.VISIBLE
                ivRadio1.setImageResource(R.drawable.ic_unselected)
                ivRadio2.setImageResource(R.drawable.ic_unselected)
                ivRadio3.setImageResource(R.drawable.ic_unselected)
                ivEdit.setImageResource(R.drawable.ic_edit)
                etClearFocus(etMsg)
                etRequestFocus(etMsg)
            }

            etMsg.setOnClickListener {
                tvMsg1.setTextColor(ciTxtColor)
                tvMsg2.setTextColor(ciTxtColor)
                tvMsg3.setTextColor(ciTxtColor)
                ivSend1.visibility = View.INVISIBLE
                ivSend2.visibility = View.INVISIBLE
                ivSend3.visibility = View.INVISIBLE
                ivSend4.visibility = View.VISIBLE
                ivRadio1.setImageResource(R.drawable.ic_unselected)
                ivRadio2.setImageResource(R.drawable.ic_unselected)
                ivRadio3.setImageResource(R.drawable.ic_unselected)
                ivEdit.setImageResource(R.drawable.ic_edit)
                etRequestFocus(etMsg)
            }
            etMsg.setOnEditorActionListener { _, actionID: Int, _ ->
                if (actionID == EditorInfo.IME_ACTION_DONE) {
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
                    ivSend1.visibility = View.INVISIBLE
                    ivSend2.visibility = View.INVISIBLE
                    ivSend3.visibility = View.INVISIBLE
                    ivSend4.visibility = View.VISIBLE
                    ivRadio1.setImageResource(R.drawable.ic_unselected)
                    ivRadio2.setImageResource(R.drawable.ic_unselected)
                    ivRadio3.setImageResource(R.drawable.ic_unselected)
                    ivEdit.setImageResource(R.drawable.ic_edit)
                }
                try {
                    if (hasFocus) {
                        v.showKeyboard()
                    } else {
                        v.hideKeyboard()
                    }
                } catch (_: Exception) {
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
                clickSend4()
            }
        }
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
                openMessage(requireContext(), message)
                etClearFocus(etMsg)
            } else {
                requireActivity().showCustomToast("Please enter message")
            }

        }
    }

    fun clickMsgLl1() {
        val ciAppColor = requireContext().getColorFromAttr(R.attr.ciAppColor, R.color.ci_app_color)
        val ciTxtColor = requireContext().getColorFromAttr(R.attr.ciTxtColor, R.color.ci_txt_color)
        with(_binding) {
            tvMsg1.setTextColor(ciAppColor)
            tvMsg2.setTextColor(ciTxtColor)
            tvMsg3.setTextColor(ciTxtColor)
            ivSend1.visibility = View.VISIBLE
            ivSend2.visibility = View.INVISIBLE
            ivSend3.visibility = View.INVISIBLE
            ivSend4.visibility = View.INVISIBLE
            ivRadio1.setImageResource(R.drawable.ic_selected)
            ivRadio2.setImageResource(R.drawable.ic_unselected)
            ivRadio3.setImageResource(R.drawable.ic_unselected)
            ivEdit.setImageResource(R.drawable.ic_edit_unselected)
        }
        etClearFocus()
    }

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
}