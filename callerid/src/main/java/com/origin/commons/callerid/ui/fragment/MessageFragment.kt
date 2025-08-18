package com.origin.commons.callerid.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.origin.commons.callerid.R
import com.origin.commons.callerid.databinding.FragmentMessageBinding
import com.origin.commons.callerid.extensions.hideKeyboard
import com.origin.commons.callerid.extensions.openMessage
import com.origin.commons.callerid.extensions.showCustomToast
import com.origin.commons.callerid.extensions.showKeyboard
import com.origin.commons.callerid.extensions.value


class MessageFragment : Fragment() {

    private val _binding by lazy {
        FragmentMessageBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
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
            val ciAppColor = ContextCompat.getColor(requireContext(), R.color.call_theme_primary)
            val ciTxtColor = ContextCompat.getColor(requireContext(), R.color.call_theme_onSurface)
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
                ivRadio1.setImageResource(R.drawable.ci_radio_unselected)
                ivRadio2.setImageResource(R.drawable.ci_radio_selected)
                ivRadio3.setImageResource(R.drawable.ci_radio_unselected)
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
                ivRadio1.setImageResource(R.drawable.ci_radio_unselected)
                ivRadio2.setImageResource(R.drawable.ci_radio_unselected)
                ivRadio3.setImageResource(R.drawable.ci_radio_selected)
                etClearFocus(etMsg)
            }
            ivEdit.setOnClickListener {
                tvMsg1.setTextColor(ciTxtColor)
                tvMsg2.setTextColor(ciTxtColor)
                tvMsg3.setTextColor(ciTxtColor)
                ivSend1.visibility = View.INVISIBLE
                ivSend2.visibility = View.INVISIBLE
                ivSend3.visibility = View.INVISIBLE
                ivSend4.visibility = View.VISIBLE
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
                ivSend1.visibility = View.INVISIBLE
                ivSend2.visibility = View.INVISIBLE
                ivSend3.visibility = View.INVISIBLE
                ivSend4.visibility = View.VISIBLE
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
                ivSend1.visibility = View.INVISIBLE
                ivSend2.visibility = View.INVISIBLE
                ivSend3.visibility = View.INVISIBLE
                ivSend4.visibility = View.VISIBLE
                ivRadio1.setImageResource(R.drawable.ci_radio_unselected)
                ivRadio2.setImageResource(R.drawable.ci_radio_unselected)
                ivRadio3.setImageResource(R.drawable.ci_radio_unselected)
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
                    ivRadio1.setImageResource(R.drawable.ci_radio_unselected)
                    ivRadio2.setImageResource(R.drawable.ci_radio_unselected)
                    ivRadio3.setImageResource(R.drawable.ci_radio_unselected)
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
                requireContext().openMessage(message)

            }

            ivSend2.setOnClickListener {
                val message = tvMsg2.text.toString()
                requireContext().openMessage(message)

            }

            ivSend3.setOnClickListener {
                val message = tvMsg3.text.toString()
                requireContext().openMessage(message)
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
                requireContext().openMessage(message)
                etClearFocus(etMsg)
            } else {
                requireActivity().showCustomToast("Please enter message")
            }

        }
    }

    fun clickMsgLl1() {
        val ciAppColor = ContextCompat.getColor(requireContext(), R.color.call_theme_primary)
        val ciTxtColor = ContextCompat.getColor(requireContext(), R.color.call_theme_onSurface)

        with(_binding) {
            tvMsg1.setTextColor(ciAppColor)
            tvMsg2.setTextColor(ciTxtColor)
            tvMsg3.setTextColor(ciTxtColor)
            ivSend1.visibility = View.VISIBLE
            ivSend2.visibility = View.INVISIBLE
            ivSend3.visibility = View.INVISIBLE
            ivSend4.visibility = View.INVISIBLE
            ivRadio1.setImageResource(R.drawable.ci_radio_selected)
            ivRadio2.setImageResource(R.drawable.ci_radio_unselected)
            ivRadio3.setImageResource(R.drawable.ci_radio_unselected)
        }
        etClearFocus()
    }
}