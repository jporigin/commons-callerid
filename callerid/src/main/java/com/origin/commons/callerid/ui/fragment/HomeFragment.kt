package com.origin.commons.callerid.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.origin.commons.callerid.CallerIdSDKApplication
import com.origin.commons.callerid.R
import com.origin.commons.callerid.databinding.FragmentHomeBinding
import com.origin.commons.callerid.extensions.emailIntent
import com.origin.commons.callerid.extensions.getAppName
import com.origin.commons.callerid.extensions.openContact
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
        val fragmentProvider = callerIdSDKApplication?.customHomeFragment
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
            context?.let { mCtx ->
                mCtx.emailIntent(mCtx.getAppName())
            }
        }
        _binding.btnSendNumber.setOnClickListener {
            context?.openContact()
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
}