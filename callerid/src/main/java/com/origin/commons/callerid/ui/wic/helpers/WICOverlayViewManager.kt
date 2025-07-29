package com.origin.commons.callerid.ui.wic.helpers

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.WindowManager
import android.view.animation.AccelerateInterpolator
import android.view.animation.OvershootInterpolator
import com.origin.commons.callerid.ads.helpers.CallerIdAds
import com.origin.commons.callerid.extensions.prefsHelper
import com.origin.commons.callerid.extensions.refreshCurrentAdsType


@SuppressLint("StaticFieldLeak")
object WICOverlayViewManager {
    private var currentState: Int = 1
    private var mWindowManager: WindowManager? = null
    private var mFloatingCallerView: WicFloatingCallerView? = null

    /*
   • 0. IDLE: No call activity.
   • 1. RINGING: Incoming call is ringing.
   • 2. OFFHOOK: A call is in progress (dialing, active, or on hold) and no other calls are ringing or waiting.
   */
    fun show(context: Context, state: Int) {
        if (mWindowManager == null) {
            mWindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        }
        if (mFloatingCallerView != null) {
            hide(context) {}
        }
        val layoutParams = getLayoutParams(context)
        currentState = state
        val view = WicFloatingCallerView(context, state, mWindowManager!!, layoutParams)
        mFloatingCallerView = view
        mWindowManager?.addView(view, layoutParams)
        view.animate()
            .alpha(1f)
            .translationX(0f)
            .setInterpolator(OvershootInterpolator(1.2f))
            .setDuration(400)
            .start()
        context.refreshCurrentAdsType()
        CallerIdAds.loadCallerIdAds(context)
    }

    private fun getLayoutParams(context: Context): WindowManager.LayoutParams {
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
            y = context.prefsHelper.wicPosition
        }
    }

    fun hide(context: Context, callback: () -> Unit) {
        mFloatingCallerView?.let { overlayView ->
            if (overlayView.isAttachedToWindow) {
                overlayView.animate()
                    .alpha(0f)
                    .translationX(context.resources.displayMetrics.widthPixels.toFloat())
                    .setDuration(300)
                    .setInterpolator(AccelerateInterpolator())
                    .withEndAction {
                        try {
                            mWindowManager?.removeView(mFloatingCallerView)
                            mFloatingCallerView = null
                            mWindowManager = null
                            overlayView.stop()
                        } catch (_: Exception) {
                        }
                    }
                    .start()
                callback.invoke()
            }
        }
        mFloatingMessageView?.let { overlayView ->
            if (overlayView.isAttachedToWindow) {
                overlayView.clearAllETFocus()
                overlayView.animate()
                    .alpha(0f)
                    .translationX(context.resources.displayMetrics.widthPixels.toFloat())
                    .setDuration(300)
                    .setInterpolator(AccelerateInterpolator())
                    .withEndAction {
                        try {
                            mWindowManager?.removeView(mFloatingMessageView)
                            mFloatingMessageView = null
                            mWindowManager = null
                        } catch (_: Exception) {
                        }
                    }
                    .start()
                callback.invoke()
            }
        }
        mFloatingReminderView?.let { overlayView ->
            if (overlayView.isAttachedToWindow) {
                overlayView.clearAllETFocus()
                overlayView.animate()
                    .alpha(0f)
                    .translationX(context.resources.displayMetrics.widthPixels.toFloat())
                    .setDuration(300)
                    .setInterpolator(AccelerateInterpolator())
                    .withEndAction {
                        try {
                            mWindowManager?.removeView(mFloatingReminderView)
                            mFloatingReminderView = null
                            mWindowManager = null
                        } catch (_: Exception) {
                        }
                    }
                    .start()
                callback.invoke()
            }
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
            y = context.prefsHelper.wicPosition
        }
    }


    private var mFloatingMessageView: WicFloatingMessageView? = null
    fun showMessagePopup(context: Context) {
        hide(context) {
            val layoutParams = getLayoutParamsMatch(context)
            val messageView = WicFloatingMessageView(context, mWindowManager!!, layoutParams) {
                // On dismiss of message popup, re-show caller overlay
                show(context, currentState)
            }
            mFloatingMessageView = messageView
            mWindowManager?.addView(messageView, layoutParams)
        }
    }

    private var mFloatingReminderView: WicFloatingReminderView? = null
    fun showReminderPopup(context: Context) {
        hide(context) {
            val layoutParams = getLayoutParamsMatch(context)
            val messageView = WicFloatingReminderView(context, mWindowManager!!, layoutParams) {
                // On dismiss of message popup, re-show caller overlay
                show(context, currentState)
            }
            mFloatingReminderView = messageView
            mWindowManager?.addView(messageView, layoutParams)
        }
    }

}
