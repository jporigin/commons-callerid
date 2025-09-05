package com.origin.commons.callerid.ui.dialog

import android.app.Activity
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.origin.commons.callerid.R
import com.origin.commons.callerid.databinding.DialogItemMessageBinding

class ConfirmationDialog(
    activity: Activity, titleId: Int = 0, titleText: String = "", message: String = "", messageId: Int = R.string.ci_caller_toggle_agree_title_2, positive: Int = R.string.ci_keep_it,
    negative: Int = R.string.ci_proceed, val cancelOnTouchOutside: Boolean = true, val callback: (result: Boolean) -> Unit
) {
    private var dialog: AlertDialog? = null

    init {
        val view = DialogItemMessageBinding.inflate(activity.layoutInflater, null, false)
        view.message.text = message.ifEmpty { activity.resources.getString(messageId) }

        val builder = MaterialAlertDialogBuilder(activity).setPositiveButton(positive) { _, _ -> positivePressed() }

        if (negative != 0) {
            builder.setNegativeButton(negative) { _, _ -> negativePressed() }
        }

        if (!cancelOnTouchOutside) {
            builder.setOnCancelListener { negativePressed() }
        }

        builder.apply {
            if (activity.isDestroyed || activity.isFinishing) {
                return@apply
            }
            dialog = builder.create().apply {
                if (titleId != 0) {
                    this.setTitle(titleId)
                } else if (titleText.isNotEmpty()) {
                    this.setTitle(titleText)
                }
                this.setView(view.root)
                this.setCancelable(cancelOnTouchOutside)
                if (!activity.isFinishing) {
                    this.setOnCancelListener {

                    }
                    this.setOnDismissListener {

                    }
                    this.setOnShowListener {

                    }
                    this.show()
                }
            }
        }
    }

    private fun positivePressed() {
        dialog?.dismiss()
        callback(true)
    }

    private fun negativePressed() {
        dialog?.dismiss()
        callback(false)
    }

}
