package com.origin.commons.callerid.ui.wic.helpers

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.origin.commons.callerid.CallerIdSDKApplication
import com.origin.commons.callerid.R
import com.origin.commons.callerid.extensions.getDefaultAppIcon
import com.origin.commons.callerid.extensions.getOpenAppIntent
import com.origin.commons.callerid.extensions.logE
import com.origin.commons.callerid.extensions.prefsHelper
import com.origin.commons.callerid.helpers.Stopwatch

@SuppressLint("ViewConstructor")
class WicFloatingCallerView(
    private val context: Context,
    private val state: Int,
    private val windowManager: WindowManager,
    private val layoutParams: WindowManager.LayoutParams,
) : LinearLayout(context) {
    private val stopwatch = Stopwatch { time ->
        updateTimerView(time)
    }
    private var isVertical = context.prefsHelper.wicIsVertical

    init {
        inflateView()
        setupClickListeners()
        setupDrag()
        stopwatch.start()
    }

    private fun inflateView() {
        removeAllViews()
        LayoutInflater.from(context).inflate(if (isVertical) R.layout.floating_caller_info_v else R.layout.floating_caller_info, this, true)
    }

    private fun setupClickListeners() {
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

        findViewById<ImageView>(R.id.ivExpandShrink)?.setOnClickListener {
            togglePopupView()
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
                    context.prefsHelper.wicPosition = layoutParams.y
                    true
                }

                else -> false
            }
        }
    }

    private fun togglePopupView() {
        this.animate().alpha(0f).setDuration(200).withEndAction { replacePopupView() }.start()
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