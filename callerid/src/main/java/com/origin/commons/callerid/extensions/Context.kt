package com.origin.commons.callerid.extensions

import android.app.ActivityManager
import android.content.ComponentName
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.IBinder
import android.text.TextUtils
import androidx.browser.customtabs.CustomTabsIntent
import com.origin.commons.callerid.CallerIdSDKApplication
import com.origin.commons.callerid.helpers.SharedPreferencesHelper
import com.origin.commons.callerid.helpers.Utils
import com.origin.commons.callerid.helpers.Utils.isScreenOverlayEnabled
import com.origin.commons.callerid.receivers.CIdLegacyForegroundService
import com.origin.commons.callerid.receivers.OgCallerIdCallReceiver
import com.origin.commons.callerid.utils.canSkipLeaveHint
import androidx.core.net.toUri

fun Context.getScreenWidthPx(): Int {
    return resources.displayMetrics.widthPixels
}

fun Context.getScreenWidthDp(): Int {
    return (resources.displayMetrics.widthPixels / this.resources.displayMetrics.density).toInt()
}

fun Context.getOpenAppIntent(): Intent? {
    val callerIdSDKApplication = try {
        this.applicationContext as? CallerIdSDKApplication
    } catch (_: Exception) {
        null
    }
    val mClass1 = callerIdSDKApplication?.openClass1
    val mClass2High = callerIdSDKApplication?.openClass2High
    return when {
        mClass1 != null && isActivityRunning(mClass1.invoke()) -> {
            Intent(this@getOpenAppIntent, mClass1.invoke())
        }

        mClass2High != null -> {
            Intent(this@getOpenAppIntent, mClass2High.invoke())
        }


        else -> null
    }
}

fun Context.registerCallReceiver() {
    try {
        if (Utils.isPhoneStatePermissionGranted(this)) {
            this.registerReceiver(OgCallerIdCallReceiver(), IntentFilter("android.intent.action.PHONE_STATE"))
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun Context.startLegacyForegroundService() {
    if (isScreenOverlayEnabled(this)) {
        Intent(this, CIdLegacyForegroundService::class.java).apply {
            try {
                val applicationContext = this@startLegacyForegroundService.applicationContext
                applicationContext.bindService(this, object : ServiceConnection {
                    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                        logE("startLegacyForegroundService::onServiceConnected")
                    }

                    override fun onServiceDisconnected(name: ComponentName?) {
                        logE("startLegacyForegroundService::onServiceDisconnected")
                    }
                }, Context.BIND_AUTO_CREATE)
                logE("startLegacyForegroundService::onServiceConnected")
            } catch (_: java.lang.Exception) {
            }
        }
    }
}

fun Context.isActivityRunning(activityClass: Class<*>): Boolean {
    val activityManager = this.getSystemService(ACTIVITY_SERVICE) as ActivityManager
    val runningTasks = activityManager.appTasks
    for (task in runningTasks) {
        val info = task.taskInfo
        if (activityClass.canonicalName == info.baseActivity?.className || activityClass.canonicalName == info.topActivity?.className) {
            return true
        }
    }
    return false
}

fun Context.getAppName(): String {
    val applicationInfo = this.applicationInfo
    val stringId = applicationInfo.labelRes
    return try {
        if (stringId == 0) {
            applicationInfo.nonLocalizedLabel.toString()
        } else {
            this.getString(stringId)
        }
    } catch (_: Exception) {
        ""
    }
}

val Context.prefsHelper: SharedPreferencesHelper get() = SharedPreferencesHelper.newInstance(this)
fun Context.getCurrentAdsType(): Int {
    this.prefsHelper.let { pref ->
        if (pref.adsRefreshType.isEmpty()) return 0
        if (pref.isPurchased) return 0
            if (pref.adsRefreshIndex < 0 || pref.adsRefreshIndex >= pref.adsRefreshType.length) {
                return 0
            }
        val currentChar = pref.adsRefreshType[pref.adsRefreshIndex]
        logE("check:getCurrentAdsType:${pref.adsRefreshType}:::${currentChar}")
        return when (currentChar) {
            '1' -> 1
            '2' -> 2
            '3' -> 3
            else -> 0
        }
    }
}

fun Context.refreshCurrentAdsType() {
    this.prefsHelper.let { pref ->
        val refreshTypeLength = pref.adsRefreshType.length
        if (refreshTypeLength > 0) {
            val newValue = (pref.adsRefreshIndex + 1) % refreshTypeLength
            pref.adsRefreshIndex = newValue
        } else {
            pref.adsRefreshIndex = -1
        }
    }
}

fun Context.getDefaultAppIcon(): Drawable? {
    val appIconDrawable = try {
        this.packageManager.getApplicationIcon(this.packageName)
    } catch (_: Exception) {
        null
    }
    return appIconDrawable
}

fun Context.getUID(): Long {
    val usedIds: MutableList<Int> = this.prefsHelper.savedReminderIds.split(",").filter { it.trim().isNotEmpty() }.map { it.trim().toInt() }.toMutableList()
    var id: Int
    do {
        id = (1000..9999).random()
    } while (usedIds.contains(id))
    usedIds.add(id)
    this.prefsHelper.savedReminderIds = TextUtils.join(",", usedIds)
    return id.toLong()
}

fun Context.openContact() {
    try {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            type = "vnd.android.cursor.dir/contact"
        }
        startActivity(intent)
    } catch (e: Exception) {
        e.printStackTrace()
        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                type = "vnd.android.cursor.dir/contact"
            }
            startActivity(intent)
        } catch (inner: Exception) {
            inner.printStackTrace()
            // Optionally show toast or fallback
        }
    }
    canSkipLeaveHint = true
}

fun Context.openMessage(message: String = "") {
    try {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            type = "vnd.android-dir/mms-sms"
        }
        if (message.isNotEmpty()) {
            intent.putExtra("sms_body", message)
        }
        startActivity(intent)
    } catch (e: Exception) {
        e.printStackTrace()
        try {
            val intent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_APP_MESSAGING)
            }
            if (message.isNotEmpty()) {
                intent.putExtra("sms_body", message)
            }
            startActivity(intent)
        } catch (inner: Exception) {
            inner.printStackTrace()
            // Optionally show toast or fallback
        }
    }
    canSkipLeaveHint = true
}

fun Context.openCalendar() {
    try {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            type = "vnd.android.cursor.item/event"
        }
        startActivity(intent)
    } catch (e: Exception) {
        e.printStackTrace()
        runCatching {
            this.showCustomToast("Failed to open calendar app")
        }
    }
    canSkipLeaveHint = true
}


fun Context.openWebBrowser(url: String) {
    try {
        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        customTabsIntent.intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        customTabsIntent.launchUrl(this, url.toUri())
    } catch (e: Exception) {
        e.printStackTrace()
        try {
            val fallbackIntent = Intent(Intent.ACTION_VIEW, url.toUri())
            startActivity(fallbackIntent)
        } catch (ex: Exception) {
            ex.printStackTrace()
            runCatching {
                this.showCustomToast("Unable to open browser")
            }
        }
    }
    canSkipLeaveHint = true
}

fun Context.emailIntent(subject: String) {
    val intent = Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "", null)).apply {
        if (subject.isNotEmpty()) {
            putExtra(Intent.EXTRA_SUBJECT, subject)
        }
    }
    try {
        startActivity(Intent.createChooser(intent, null))
    } catch (e: Exception) {
        e.printStackTrace()
    }
    canSkipLeaveHint = true
}