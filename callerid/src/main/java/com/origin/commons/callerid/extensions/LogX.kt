/*
 * March 03, 2025  12:01 PM
 */

package com.origin.commons.callerid.extensions

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.StringRes
import com.origin.commons.callerid.CallerIdSDKApplication.Companion.getFirebaseAnalytics

const val mDefaultTagString = " ogCallerID "

fun logE(mMessageString: String?) {
    try {
        Log.e(mDefaultTagString, "$mMessageString")
    } catch (_: Exception) {
        systemLogE(mMessageString)
    }
}

fun logE(tagString: String?, mMessageString: String?) {
    if (tagString.isNullOrEmpty()) {
        logE(mMessageString)
    } else {
        try {
            Log.e(tagString, "$mMessageString")
        } catch (_: Exception) {
            systemLogE(mMessageString)
        }
    }
}

fun Context.logEventE(msg: String) {
    logE("FA-SVC", "name = ${msg.normalizeText()}")
    try {
        getFirebaseAnalytics().logEvent(msg.normalizeText(), Bundle())
    } catch (_: Exception) {
    }
}

@SuppressLint("LogNotTimber")
fun systemLogE(messageString: String?) {
    try {
        Log.e(mDefaultTagString, "$messageString")
    } catch (_: Exception) {
    }
}

@SuppressLint("LogNotTimber")
fun systemLogE(tag: String, messageString: String?) {
    try {
        Log.e(tag, "$messageString")
    } catch (_: Exception) {
    }
}

fun Activity.showCustomToast(message: String, length: Int = Toast.LENGTH_SHORT) {
    runOnUiThread {
        Toast.makeText(this, message, length).show()
    }
}

fun Activity.showCustomToast(@StringRes message: Int, length: Int = Toast.LENGTH_SHORT) {
    runOnUiThread {
        Toast.makeText(this, getString(message), length).show()
    }
}
fun String.normalizeText(): String {
    if (length >= 29) {
        return substring(0, 29)
    }
    return toString()
}