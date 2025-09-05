package com.origin.commons.callerid.ui.wic.helpers

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.telephony.PhoneNumberUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.origin.commons.callerid.CallerIdSDKApplication
import com.origin.commons.callerid.R
import com.origin.commons.callerid.extensions.getContactNameFromNumber
import com.origin.commons.callerid.extensions.getDefaultAppIcon
import com.origin.commons.callerid.extensions.getOpenAppIntent
import com.origin.commons.callerid.extensions.logE
import com.origin.commons.callerid.extensions.prefsHelper
import com.origin.commons.callerid.helpers.Stopwatch
import com.origin.commons.callerid.model.PopViewType
import com.origin.commons.callerid.utils.callType

@SuppressLint("ViewConstructor")
class WicFloatingCallerView(
    private val context: Context,
    private val state: Int,
    viewType: PopViewType,
    private val windowManager: WindowManager,
    private var layoutParams: WindowManager.LayoutParams,
) : LinearLayout(context) {
    private val stopwatch = Stopwatch { time ->
        updateTimerView(time)
    }
    private var isVertical = context.prefsHelper.wicIsVertical
    private var popViewType = viewType

    init {
        inflateView()
        setupClickListeners()
        setupDrag()
        stopwatch.start()
    }

    private fun inflateView() {
        removeAllViews()
        val layoutRes = when(popViewType) {
            PopViewType.CLASSIC -> R.layout.floating_caller_info_classic
            PopViewType.STANDARD -> if (isVertical) R.layout.floating_caller_info_v else R.layout.floating_caller_info
        }
        LayoutInflater.from(context).inflate(layoutRes, this, true)
    }

    private fun setupClickListeners() {
        findViewById<TextView>(R.id.tvPrivateNumber)?.apply {
            val phoneNumber = context.prefsHelper.callPhoneNumber.takeIf { it != "Unknown" }
            text = if (!phoneNumber.isNullOrEmpty()) {
                val normalizeNumber = PhoneNumberUtils.normalizeNumber(phoneNumber)
                normalizeNumber?.let { number ->
                    context.getContactNameFromNumber(number) ?: context.getString(R.string.ci_private_number)
                } ?: context.getString(R.string.ci_private_number)
            } else {
                context.getString(R.string.ci_private_number)
            }
        }

        findViewById<ImageView>(R.id.ivMuteRingtoneMic)?.apply {
            refreshIvMuteRingtoneMic()
            setOnClickListener {
                if (state == 1) {
                    WicAudioManager.toggleRingtoneState(context)
                } else {
                    WicAudioManager.toggleMicState(context)
                }
                refreshIvMuteRingtoneMic()
            }
        }

        findViewById<TextView>(R.id.tvCallType)?.text = callType

        when(popViewType) {
            PopViewType.CLASSIC -> {
                findViewById<ImageView>(R.id.ivClosePop)?.setOnClickListener {
                    if (context.prefsHelper.showStandardPopupOnClassicClose) {
                        toggleClassicToStandardView()
                    } else {
//                        WICOverlayViewManager.hide(context) {
//                            WICController.isManuallyClosed = true
//                        }
                        WICOverlayViewManager.hideTemporary()
                    }
                }
            }
            PopViewType.STANDARD -> {
                findViewById<ImageView>(R.id.ivExpandShrink)?.setOnClickListener {
                    togglePopupView()
                }
            }
        }

        findViewById<LinearLayout>(R.id.cvLogo)?.setOnClickListener {
            context.getOpenAppIntent()?.let { intent ->
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
            }
        }
        findViewById<ImageView>(R.id.ivMessage)?.setOnClickListener {
            WICOverlayViewManager.showMessagePopup(context)
        }
        findViewById<ImageView>(R.id.ivReminder)?.setOnClickListener {
            WICOverlayViewManager.showReminderPopup(context)
        }
        setUpLogo(findViewById(R.id.ivLogo))
    }

    private fun refreshIvMuteRingtoneMic() {
        WicAudioManager.initAudioManager(context)
        val res = if (state == 1) {
            if (WicAudioManager.mIsRingtoneMuted) R.drawable.ci_volume else R.drawable.ci_volume_off
        } else {
            logE("::${WicAudioManager.mIsMicMuted}")
            if (WicAudioManager.mIsMicMuted) R.drawable.ci_mic_on else R.drawable.ci_mic_off
        }
        findViewById<ImageView>(R.id.ivMuteRingtoneMic)?.setImageResource(res)
    }

    private fun setUpLogo(ivLogo: ImageView) {
        val customAppLogoResId = (context.applicationContext as? CallerIdSDKApplication)?.appLogo
        try {
            if (customAppLogoResId != null && customAppLogoResId != 0) {
                ivLogo.setImageResource(customAppLogoResId)
            } else {
                context.getDefaultAppIcon()?.let { ivLogo.setImageDrawable(it) }
            }
        } catch (_: Exception) {
        }
    }

    private fun updateTimerView(time: String) {
        findViewById<TextView>(R.id.tvCallDuration)?.text = "Duration $time"
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupDrag() {
        val displayMetrics = context.resources.displayMetrics
        var initialY = 0
        var initialTouchY = 0f
        var isDragging = false


        findViewById<View>(R.id.llWicMain)?.setOnTouchListener { _, event ->
            val params = this.layoutParams
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialY = params.y
                    initialTouchY = event.rawY
                    isDragging = false
                    true
                }

                MotionEvent.ACTION_MOVE -> {
                    val deltaY = (event.rawY - initialTouchY).toInt()

                    if (!isDragging && kotlin.math.abs(deltaY) > 10) {
                        isDragging = true
                        animate()?.alpha(0.58f)?.setDuration(100)?.start()
                    }

                    params.y = (initialY + deltaY).coerceIn(
                        0,
                        displayMetrics.heightPixels - this.height
                    )
                    windowManager.updateViewLayout(this, params)
                    true
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    if (isDragging) {
                        animate()?.alpha(1f)?.setDuration(150)?.start()
                    }
                    isDragging = false
                    when (popViewType) {
                        PopViewType.CLASSIC -> context.prefsHelper.wicClassicPosition = layoutParams.y
                        PopViewType.STANDARD ->  context.prefsHelper.wicStandardPosition = layoutParams.y
                    }
                    true
                }

                else -> false
            }
        }
    }

//    private fun togglePopupView() {
//        this.animate().alpha(0f).setDuration(200).withEndAction { replacePopupView() }.start()
//    }

    private fun toggleClassicToStandardView() {
        this.animate().alpha(0f).setDuration(200).withEndAction { replaceClassicView() }.start()
    }

    private fun replaceClassicView() {
        try {
            if (this.isAttachedToWindow) {
                windowManager.removeView(this)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        popViewType = PopViewType.STANDARD
        WICOverlayViewManager.popViewType = popViewType
        this.layoutParams = getStandardLayoutParams(context)

        // inflate new orientation
        inflateView()
        setupClickListeners()
        setupDrag()

        try {
            windowManager.addView(this, layoutParams)
            this.alpha = 0f
            this.animate().alpha(1f).setDuration(200).start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getStandardLayoutParams(context: Context): WindowManager.LayoutParams {
        val flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED // ← NO EFFECT on overlays
        return WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_PHONE, flags,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.END or Gravity.TOP
            x = 16
            y = context.prefsHelper.wicStandardPosition
        }
    }

    private fun togglePopupView() {
        this.animate()
            .scaleX(0f)
            .scaleY(0.9f)
            .alpha(0f)
            .setDuration(150)
            .withEndAction {
                replacePopupView()
                this.scaleX = 0f
                this.scaleY = 0.9f
                this.alpha = 0f
                this.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .alpha(1f)
                    .setDuration(150)
                    .start()
            }
            .start()
    }

    private fun replacePopupView() {
        try {
            if (this.isAttachedToWindow) {
                windowManager.removeView(this)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // toggle orientation
        isVertical = !isVertical
        context.prefsHelper.wicIsVertical = isVertical

        // inflate new orientation
        inflateView()
        setupClickListeners()
        setupDrag()

        try {
            windowManager.addView(this, layoutParams)
            this.alpha = 0f
            this.animate().alpha(1f).setDuration(200).start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stop() {
        stopwatch.reset()
    }
}