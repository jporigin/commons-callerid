package com.origin.commons.callerid.ui.wic.helpers

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.view.animation.AccelerateInterpolator
import android.view.animation.OvershootInterpolator
import com.origin.commons.callerid.ads.helpers.CallerIdAds
import com.origin.commons.callerid.extensions.logE
import com.origin.commons.callerid.extensions.prefsHelper
import com.origin.commons.callerid.extensions.refreshCurrentAdsType
import com.origin.commons.callerid.helpers.CallerIdUtils
import com.origin.commons.callerid.model.PopViewType


@SuppressLint("StaticFieldLeak")
object WICOverlayViewManager {
    private var currentState: Int = 1
    private var mWindowManager: WindowManager? = null
    private var mFloatingCallerView: WicFloatingCallerView? = null

    private var mFloatingMessageView: WicFloatingMessageView? = null

    private var mFloatingReminderView: WicFloatingReminderView? = null

    var popViewType: PopViewType? = null

    /**
    • 0. IDLE: No call activity.
    • 1. RINGING: Incoming call is ringing.
    • 2. OFFHOOK: A call is in progress (dialing, active, or on hold) and no other calls are ringing or waiting.
     */
    fun show(context: Context, state: Int) {
        if (!CallerIdUtils.isScreenOverlayEnabled(context)) {
            return
        }
        if (mWindowManager == null) {
            mWindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        }
        if (mFloatingCallerView != null) {
            hide(context) {}
        }
        val popViewType = popViewType ?: context.prefsHelper.popViewType
        val layoutParams = when (popViewType) {
            PopViewType.CLASSIC -> getClassicLayoutParams(context)
            PopViewType.STANDARD -> getStandardLayoutParams(context)
        }
        currentState = state
        val view = WicFloatingCallerView(context, state, popViewType, mWindowManager!!, layoutParams)
        mFloatingCallerView = view
        try {
            mWindowManager?.addView(view, layoutParams)
        } catch (e: WindowManager.BadTokenException) {
            logE("Unable to add overlay view $e")
        }
        view.animate()
            .alpha(1f)
            .translationX(0f)
            .setInterpolator(OvershootInterpolator(1.2f))
            .setDuration(400)
            .start()
        context.refreshCurrentAdsType()
        CallerIdAds.loadCallerIdAds(context)
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

    private fun getClassicLayoutParams(context: Context): WindowManager.LayoutParams {
        val flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED // ← No effect on overlays

        return WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT, // Classic is wide
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_PHONE,
            flags,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.CENTER
            x = 10
            y = context.prefsHelper.wicClassicPosition
        }
    }

    fun hide(context: Context, callback: () -> Unit) {
        val screenWidth = context.resources.displayMetrics.widthPixels.toFloat()
        val overlayViews = listOfNotNull(mFloatingCallerView, mFloatingMessageView, mFloatingReminderView)
        // If no overlays, finish immediately
        if (overlayViews.isEmpty()) {
            callback()
            return
        }
        var removedCount = 0
        val total = overlayViews.size

        fun onRemoved() {
            removedCount++
            if (removedCount == total) {
                mWindowManager = null
                callback()
            }
        }

        overlayViews.forEach { overlayView ->
            if (overlayView.isAttachedToWindow) {
                overlayView.animate().alpha(0f).translationX(screenWidth).setDuration(150).setInterpolator(AccelerateInterpolator()).withEndAction {
                    try {
                        mWindowManager?.removeView(overlayView)
                        when (overlayView) {
                            mFloatingCallerView -> {
                                mFloatingCallerView?.stop()
                                mFloatingCallerView = null
                            }

                            mFloatingMessageView -> {
                                mFloatingMessageView?.clearAllETFocus()
                                mFloatingMessageView = null
                            }

                            mFloatingReminderView -> {
                                mFloatingReminderView?.clearAllETFocus()
                                mFloatingReminderView = null
                            }
                        }
                    } catch (_: Exception) {
                    } finally {
                        onRemoved()
                    }
                }.start()
            } else {
                // Not attached, treat as removed
                onRemoved()
            }
        }
    }

    fun hideTemporary() {
        mFloatingCallerView?.let { view ->
            view.visibility = View.INVISIBLE
        }
    }

    private fun getLayoutParamsMatch(context: Context): WindowManager.LayoutParams {
        val flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED // ← NO EFFECT on overlays
        return WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT,
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

    fun showMessagePopup(context: Context) {
        if (!CallerIdUtils.isScreenOverlayEnabled(context)) {
            return
        }
        hide(context) {
            if (mWindowManager == null) {
                mWindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            }
            val layoutParams = getLayoutParamsMatch(context)
            val messageView = WicFloatingMessageView(context, mWindowManager!!, layoutParams) {
                // On dismiss of message popup, re-show caller overlay
                show(context, currentState)
            }
            mFloatingMessageView = messageView
            try {
                mWindowManager?.addView(messageView, layoutParams)
            } catch (e: WindowManager.BadTokenException) {
                logE("Unable to add overlay message view $e")
            }
        }
    }

    fun showReminderPopup(context: Context) {
        if (!CallerIdUtils.isScreenOverlayEnabled(context)) {
            return
        }
        hide(context) {
            if (mWindowManager == null) {
                mWindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            }
            val layoutParams = getLayoutParamsMatch(context)
            val reminderView = WicFloatingReminderView(context, mWindowManager!!, layoutParams) {
                // On dismiss of message popup, re-show caller overlay
                show(context, currentState)
            }
            mFloatingReminderView = reminderView
            try {
                mWindowManager?.addView(reminderView, layoutParams)
            } catch (e: WindowManager.BadTokenException) {
                logE("Unable to add overlay reminder view $e")
            }

        }
    }

}
