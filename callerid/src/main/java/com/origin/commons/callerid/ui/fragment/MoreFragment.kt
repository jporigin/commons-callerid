package com.origin.commons.callerid.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.origin.commons.callerid.databinding.FragmentMoreBinding
import com.origin.commons.callerid.extensions.emailIntent
import com.origin.commons.callerid.extensions.getAppName
import com.origin.commons.callerid.extensions.openCalendar
import com.origin.commons.callerid.extensions.openCallerIDSetting
import com.origin.commons.callerid.extensions.openContact
import com.origin.commons.callerid.extensions.openMessage
import com.origin.commons.callerid.extensions.openWebBrowser


class MoreFragment : Fragment() {

    private val _binding by lazy {
        FragmentMoreBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        init()
        return _binding.root
    }

    private fun init() {
        clickEvents()
    }

    private fun clickEvents() {
        with(_binding) {
            clAddCallerToYourContacts.setOnClickListener {
                context?.openContact()
            }
            clMessage.setOnClickListener {
                context?.openMessage()
            }
            clSendMail.setOnClickListener {
                context?.let { mCtx ->
                    mCtx.emailIntent(mCtx.getAppName())
                }
            }
            clCalender.setOnClickListener {
                context?.openCalendar()
            }
            clWeb.setOnClickListener {
                context?.openWebBrowser("http://www.google.com")
            }
            clAppSetting.setOnClickListener {
                activity?.openCallerIDSetting()
            }
        }
    }
}