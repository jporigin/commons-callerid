package com.origin.commons.callerid.ui.dialog

import android.app.Activity
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.WindowManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.origin.commons.callerid.R
import com.origin.commons.callerid.databinding.DialogChoosePopupBinding
import com.origin.commons.callerid.extensions.prefsHelper
import com.origin.commons.callerid.extensions.withAlpha
import com.origin.commons.callerid.model.PopViewType

class ChoosePopupDialog(private val activity: Activity) {

    private var bottomSheetDialog: BottomSheetDialog? = null
    private var popViewType: PopViewType = activity.prefsHelper.popViewType

    init {
        val view = DialogChoosePopupBinding.inflate(activity.layoutInflater, null, false)
        bottomSheetDialog = BottomSheetDialog(activity).apply {
            setContentView(view.root)
            setCancelable(true)
            setCanceledOnTouchOutside(true)
            window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            show()
        }

        setUpPopupView(view)
        setUpClickListeners(view)

        view.btnApply.setOnClickListener {
            activity.prefsHelper.popViewType = popViewType
            bottomSheetDialog?.dismiss()
        }

        view.btnClose.setOnClickListener {
            bottomSheetDialog?.dismiss()
        }
    }

    private fun setUpPopupView(binding: DialogChoosePopupBinding) {
        when (popViewType) {
            PopViewType.CLASSIC -> {
                activity.getColor(R.color.call_theme_primary).let { color ->
                    binding.classicView.apply {
                        setCardBackgroundColor(ColorStateList.valueOf(color.withAlpha(0x4D)))
                        strokeColor = color
                    }
                    binding.tvClassicPopup.setTextColor(color)
                }
                binding.standardView.apply {
                    setCardBackgroundColor(ColorStateList.valueOf(Color.TRANSPARENT))
                    strokeColor = activity.getColor(R.color.call_theme_onSurfaceVariant)
                }
                binding.tvStandardPopup.setTextColor(activity.getColor(R.color.call_theme_onSurface))
            }

            PopViewType.STANDARD -> {
                activity.getColor(R.color.call_theme_primary).let { color ->
                    binding.standardView.apply {
                        setCardBackgroundColor(ColorStateList.valueOf(color.withAlpha(0x4D)))
                        strokeColor = color
                    }
                    binding.tvStandardPopup.setTextColor(color)
                }
                binding.classicView.apply {
                    setCardBackgroundColor(ColorStateList.valueOf(Color.TRANSPARENT))
                    strokeColor = activity.getColor(R.color.call_theme_onSurfaceVariant)
                }
                binding.tvClassicPopup.setTextColor(activity.getColor(R.color.call_theme_onSurface))
            }
        }
    }

    private fun setUpClickListeners(binding: DialogChoosePopupBinding) {
        binding.classicView.setOnClickListener {
            popViewType = PopViewType.CLASSIC
            setUpPopupView(binding)
        }
        binding.standardView.setOnClickListener {
            popViewType = PopViewType.STANDARD
            setUpPopupView(binding)
        }
    }

}