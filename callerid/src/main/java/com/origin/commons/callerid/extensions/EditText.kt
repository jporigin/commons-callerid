package com.origin.commons.callerid.extensions

import android.text.Editable
import android.text.TextWatcher
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