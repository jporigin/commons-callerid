package com.origin.commons.callerid.sample.extensions

import android.content.Context
import android.content.Intent
import com.ogmediaapps.callerid.helpers.SharedPreferencesHelper
import kotlin.apply


fun Context.setPermissionGranted(value: Boolean) = SharedPreferencesHelper.setBooleanPref(this, SharedPreferencesHelper.PERMISSIONS_GRANTED, value)

fun Context.isPermissionRequestAsked(): Boolean = SharedPreferencesHelper.getBooleanPref(this, SharedPreferencesHelper.PHONE_PERMISSION_REQUEST_ASKED)

fun Context.setPermissionRequestAsked(value: Boolean) = SharedPreferencesHelper.setBooleanPref(this, SharedPreferencesHelper.PHONE_PERMISSION_REQUEST_ASKED, value)

fun Context.isNotiPermissionReqAsked(): Boolean = SharedPreferencesHelper.getBooleanPref(this, SharedPreferencesHelper.NOTI_PERMISSION_REQUEST_ASKED)

fun Context.setNotiPermissionReqAsked(value: Boolean) = SharedPreferencesHelper.setBooleanPref(this, SharedPreferencesHelper.NOTI_PERMISSION_REQUEST_ASKED, value)

fun Context.setScreenOverlayEnabled(value: Boolean) = SharedPreferencesHelper.setBooleanPref(this, SharedPreferencesHelper.SCREEN_OVERLAY_PERMISSION_GRANTED, value)

fun Context.startIntent(destinationClass: Class<*>) = Intent().apply {
    setClass(this@startIntent, destinationClass)
    startActivity(this)
}

fun Context.startIntentWithFlags(destinationClass: Class<*>, flags: Int) = Intent().apply {
    setClass(this@startIntentWithFlags, destinationClass)
    this.flags = flags
    startActivity(this)
}