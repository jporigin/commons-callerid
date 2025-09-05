package com.origin.commons.callerid.sample.ui.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.origin.commons.callerid.extensions.getOpenAppIntent
import com.origin.commons.callerid.sample.databinding.FragmentCiHomeScreenBinding

class CIHomeScreenFragment : Fragment() {

    private val binding by lazy {
        FragmentCiHomeScreenBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val viewToastMap = listOf(binding.v1, binding.v2, binding.v3, binding.v4, binding.v5)
        viewToastMap.forEach { view ->
            view.setOnClickListener {
                this@CIHomeScreenFragment.activity?.getOpenAppIntent()?.let { intent ->
                    intent.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or
                                Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    this@CIHomeScreenFragment.activity?.finish()
                }
            }
        }
        return binding.root
    }

}