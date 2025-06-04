package com.origin.commons.callerid.ui.fragment

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.origin.commons.callerid.databinding.FragmentMoreBinding
import com.origin.commons.callerid.extensions.showCustomToast


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
                openContact(requireContext())
            }

            clMessage.setOnClickListener {
                openMessage(requireContext())
            }

            clSendMail.setOnClickListener {
                emailIntent(requireContext(), "")
            }

            clCalender.setOnClickListener {
                openCalendar(requireContext())
            }

            clWeb.setOnClickListener {
                openWebBrowser(requireContext(), "http://www.google.com")
            }
        }
    }

    private fun openContact(context: Context) {
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setType("vnd.android.cursor.dir/contact")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            val intent2 = Intent()
            intent2.setAction(Intent.ACTION_VIEW)
            intent2.setType("vnd.android.cursor.dir/contact")
            startActivity(intent2)
        }
    }

    private fun openMessage(context: Context) {
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.type = "vnd.android-dir/mms-sms"
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            val intent2 = Intent(Intent.ACTION_MAIN)
            intent2.addCategory(Intent.CATEGORY_APP_MESSAGING)
            intent2.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent2)
        }
    }

    private fun openEmail(context: Context) {
        try {
            val intent = Intent(Intent.ACTION_SEND_MULTIPLE)
            intent.type = "message/rfc822"
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            val intent2 = Intent(Intent.ACTION_MAIN)
            intent2.addCategory(Intent.CATEGORY_APP_EMAIL)
            intent2.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent2)
        }
    }

    private fun openCalendar(context: Context) {
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.type = "vnd.android.cursor.item/event"
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            // If opening default calendar app fails, you can handle it here
            requireActivity().showCustomToast("Failed to open calendar app")
        }
    }

    private fun openWebBrowser(context: Context, url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            // If opening web browser fails, you can handle it here
            requireActivity().showCustomToast("Failed to open web browser")
        }
    }

    private fun emailIntent(context: Context, subject: String) {
        val intent = Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "", null)).apply {
            putExtra(Intent.EXTRA_SUBJECT, subject)
        }
        try {
            context.startActivity(Intent.createChooser(intent, null))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}