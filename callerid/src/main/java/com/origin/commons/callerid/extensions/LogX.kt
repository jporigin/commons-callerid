package com.origin.commons.callerid.extensions

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
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

fun logEventE(msg: String) {
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

fun Context.showCustomToast(message: String, length: Int = Toast.LENGTH_SHORT) {
    Handler(mainLooper).post {
        Toast.makeText(this, message, length).show()
    }
}

fun Context.showCustomToast(@StringRes message: Int, length: Int = Toast.LENGTH_SHORT) {
    Handler(mainLooper).post {
        Toast.makeText(this, getString(message), length).show()
    }
}
fun String.normalizeText(): String {
    if (length >= 29) {
        return substring(0, 29)
    }
    return toString()
}