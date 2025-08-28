package com.origin.commons.callerid.ui.wic

import android.content.Context
import com.origin.commons.callerid.ui.wic.helpers.WICOverlayViewManager

object WICController {

    /*
     • 0. IDLE: No call activity.
     • 1. RINGING: Incoming call is ringing.
     • 2. OFFHOOK: A call is in progress (dialing, active, or on hold) and no other calls are ringing or waiting.
     */

    private var isOverlayVisible = false
//    var isManuallyClosed = false

    fun showPopup(context: Context,state: Int) {
        if (!isOverlayVisible /*&& !isManuallyClosed*/) {
            isOverlayVisible = true
            WICOverlayViewManager.show(context ,state)
        }
    }

    fun destroyPopup(context: Context, callback: () -> Unit) {
        if (isOverlayVisible /*&& !isManuallyClosed*/) {
            isOverlayVisible = false
//            isManuallyClosed = false
            WICOverlayViewManager.popViewType = null
            WICOverlayViewManager.hide(context, callback)
        }
//        else {
//            isManuallyClosed = false
//            isOverlayVisible = false
//            WICOverlayViewManager.popViewType = null
//            callback.invoke()
//        }
    }
}