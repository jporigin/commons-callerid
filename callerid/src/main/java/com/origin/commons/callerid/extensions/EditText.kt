package com.origin.commons.callerid.extensions

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

val EditText.value: String get() = text.toString().trim()

fun EditText.onTextChangeListener(listener: (String?) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            listener(s.toString())
        }

        override fun afterTextChanged(s: Editable?) {}
    })
}

//fun EditText.showKeyboard() {
//    requestFocus()
//    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//    imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
//    imm.hideSoftInputFromWindow(this, InputMethodManager.SHOW_IMPLICIT)
//}