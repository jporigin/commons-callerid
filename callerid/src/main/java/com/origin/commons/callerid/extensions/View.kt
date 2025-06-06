package com.origin.commons.callerid.extensions

import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat


fun View.beInvisibleIf(beInvisible: Boolean) = if (beInvisible) beInvisible() else beVisible()

fun View.beVisibleIf(beVisible: Boolean) = if (beVisible) beVisible() else beGone()

fun View.beGoneIf(beGone: Boolean) = beVisibleIf(!beGone)

fun View.beInvisible() {
    visibility = View.INVISIBLE
}
fun View.beVisible() {
    this.visibility = View.VISIBLE
}

fun View.beGone() {
    this.visibility = View.GONE
}


fun View.showKeyboard() {
    try {
        val imm = ContextCompat.getSystemService(context, InputMethodManager::class.java)
        imm?.showSoftInput(this, InputMethodManager.SHOW_FORCED)
    } catch (e: Exception) {
        Log.e("ViewExtensions", "Error showing keyboard", e)
    }
}

fun View.hideKeyboard() {
    try {
        val imm = ContextCompat.getSystemService(context, InputMethodManager::class.java)
        imm?.hideSoftInputFromWindow(this.windowToken, 0)
    } catch (e: Exception) {
        Log.e("ViewExtensions", "Error hiding keyboard", e)
    }
}