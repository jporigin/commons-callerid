package com.origin.commons.callerid.ui.fragment

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.origin.commons.callerid.CallerIdSDKApplication
import com.origin.commons.callerid.R
import com.origin.commons.callerid.databinding.FragmentHomeBinding
import java.util.Calendar

class HomeFragment : Fragment() {
    private val _binding by lazy {
        FragmentHomeBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        init()
        return _binding.root
    }


    private fun init() {
        val callerIdSDKApplication = try {
            this@HomeFragment.activity?.application as? CallerIdSDKApplication
        } catch (_: Exception) {
            null
        }
        val fragmentProvider = callerIdSDKApplication?.customFragmentProvider
        if (fragmentProvider != null) {
            _binding.llCiDefault.visibility = View.GONE
            _binding.flCiCustom.visibility = View.VISIBLE
            childFragmentManager.beginTransaction().replace(_binding.flCiCustom.id, fragmentProvider.invoke()).commitAllowingStateLoss()
        } else {
            _binding.flCiCustom.visibility = View.GONE
            _binding.llCiDefault.visibility = View.VISIBLE
            initDefaultView()
            clickEvents()
        }
    }

    private fun initDefaultView() {
        _binding.tvGreetingText.text = getGreetingMessage()
        _binding.llDefault.setOnClickListener {
            this@HomeFragment.activity?.apply {
                Toast.makeText(this, getGreetingMessage(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun clickEvents() {
        _binding.btnAddEmail.setOnClickListener {
            openContact(requireContext())
        }
        _binding.btnSendNumber.setOnClickListener {
            emailIntent(requireContext(), "")
        }
    }

    private fun getGreetingMessage(): String {
        val c = Calendar.getInstance()
        val timeOfDay = c.get(Calendar.HOUR_OF_DAY)
        return when (timeOfDay) {
            in 0..11 -> getString(R.string.good_morning)
            in 12..15 -> getString(R.string.good_afternoon)
            in 16..20 -> getString(R.string.good_evening)
            in 21..23 -> getString(R.string.good_night)
            else -> getString(R.string.good_morning)
        }
    }

    private fun openContact(context: Context) {
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setType("vnd.android.cursor.dir/contact")
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