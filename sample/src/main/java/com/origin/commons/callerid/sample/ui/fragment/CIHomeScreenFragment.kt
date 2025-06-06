package com.origin.commons.callerid.sample.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.origin.commons.callerid.extensions.getOpenAppIntent
import com.origin.commons.callerid.sample.databinding.FragmentCiHomeScreenBinding
import kotlin.collections.forEach

class CIHomeScreenFragment : Fragment() {

    private val binding by lazy {
        FragmentCiHomeScreenBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val viewToastMap = listOf(binding.v1, binding.v2, binding.v3, binding.v4, binding.v5)
        viewToastMap.forEach { view ->
            view.setOnClickListener {
                this@CIHomeScreenFragment.activity?.getOpenAppIntent()?.let { intent ->
                    startActivity(intent)
                    this@CIHomeScreenFragment.activity?.finish()
                }
            }
        }
        return binding.root
    }

}