package com.origin.commons.callerid.extensions

import android.widget.EditText

val EditText.value: String get() = text.toString().trim()
