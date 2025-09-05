package com.origin.commons.callerid.extensions

import android.app.ActivityManager
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.IBinder
import android.provider.ContactsContract
import android.text.TextUtils
import android.util.TypedValue
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.origin.commons.callerid.CallerIdSDKApplication
import com.origin.commons.callerid.helpers.CallerIdSDK
import com.origin.commons.callerid.helpers.CallerIdUtils
import com.origin.commons.callerid.helpers.CallerIdUtils.isScreenOverlayEnabled
import com.origin.commons.callerid.helpers.SharedPreferencesHelper
import com.origin.commons.callerid.receivers.CIdLegacyForegroundService
import com.origin.commons.callerid.receivers.OgCallerIdCallReceiver

fun <T> tryOrNull(logOnError: Boolean = true, body: () -> T?): T? {
    return try {
        body()
    } catch (e: Exception) {
        if (logOnError) {
            logE("tryOrNull $e")
        }
        null
    }
}

fun Context.getColorCompat(colorRes: Int): Int {
    return tryOrNull { ContextCompat.getColor(this, colorRes) } ?: Color.BLACK
}

fun Context.resolveThemeColor(attributeId: Int, default: Int = 0): Int {
    val outValue = TypedValue()
    val wasResolved = theme.resolveAttribute(attributeId, outValue, true)
    return if (wasResolved) getColorCompat(outValue.resourceId) else default
}

fun AppCompatActivity.enableThemeEdgeToEdge(
    @ColorInt statusBarScrim: Int,
    @ColorInt navigationBarScrim: Int,
    isForceDark: Boolean = false
) {
    if (isForceDark) {
        systemBarDarkStyle(statusBarScrim, navigationBarScrim)
    } else {
        systemBarLightStyle(statusBarScrim, navigationBarScrim)
    }
}

private fun AppCompatActivity.systemBarDarkStyle(
    @ColorInt statusBarScrim: Int,
    @ColorInt navigationBarScrim: Int
) {
    enableEdgeToEdge(
        statusBarStyle = SystemBarStyle.dark(statusBarScrim),
        navigationBarStyle = SystemBarStyle.dark(navigationBarScrim)
    )
}

private fun AppCompatActivity.systemBarLightStyle(
    @ColorInt statusBarScrim: Int,
    @ColorInt navigationBarScrim: Int
) {
    enableEdgeToEdge(
        statusBarStyle = SystemBarStyle.light(statusBarScrim, Color.TRANSPARENT),
        navigationBarStyle = SystemBarStyle.light(navigationBarScrim, Color.TRANSPARENT)
    )
}

fun Context.isDarkMode(): Boolean {
    val nightModeFlags = this.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
    return nightModeFlags == Configuration.UI_MODE_NIGHT_YES
}

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
        mClass1 != null && isActivityRunning(mClass1.provide()) -> {
            Intent(this@getOpenAppIntent, mClass1.provide())
        }

        mClass2High != null -> {
            Intent(this@getOpenAppIntent, mClass2High.provide())
        }

        else -> null
    }
}

fun Context.registerCallReceiver() {
    try {
        if (CallerIdUtils.isPhoneStatePermissionGranted(this)) {
            this.registerReceiver(
                OgCallerIdCallReceiver(),
                IntentFilter("android.intent.action.PHONE_STATE")
            )
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

internal val Context.prefsHelper: SharedPreferencesHelper
    get() = SharedPreferencesHelper.newInstance(
        this
    )

fun Context.getCurrentAdsType(): Int {
    this.prefsHelper.let { pref ->
        //
        if (pref.adsRefreshType.isEmpty()) return 0
        //
        if (pref.isPurchased) return 0
        //
        if (pref.adsRefreshIndex < 0 || pref.adsRefreshIndex >= pref.adsRefreshType.length) {
            return 0
        }
        //
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
    val usedIds: MutableList<Int> =
        this.prefsHelper.savedReminderIds.split(",").filter { it.trim().isNotEmpty() }
            .map { it.trim().toInt() }.toMutableList()
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
    } catch (e: ActivityNotFoundException) {
        e.printStackTrace()
    } catch (e: Exception) {
        e.printStackTrace()
        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                type = "vnd.android.cursor.dir/contact"
            }
            startActivity(intent)
        } catch (inner: Exception) {
            inner.printStackTrace()
        }
    }
}

fun Context.makePhoneCall(phoneNumber: String?, callback: () -> Unit) {
    val callerIdSDKApplication = try {
        applicationContext as? CallerIdSDKApplication
    } catch (_: Exception) {
        null
    }

    try {
        if (CallerIdSDK.isHostDefaultDialerApp()) {
            val openClassForDefaultApp = callerIdSDKApplication?.openClassForDefaultApp
            if (openClassForDefaultApp != null) {
                Intent(this, openClassForDefaultApp.provide()).apply {
                    if (!phoneNumber.isNullOrEmpty()) {
                        putExtra("phone_number", phoneNumber)
                        putExtra(Intent.EXTRA_TEXT, phoneNumber)
                    }
                    putExtra("isFromCallerId", true)
                    startActivity(this)
                }
            } else {
                launchDialerIntent(phoneNumber)
            }
        } else {
            launchDialerIntent(phoneNumber)
        }
    } catch (_: ActivityNotFoundException) {
        launchDialerIntent(phoneNumber)
    } catch (_: Exception) {
        launchDialerIntent(phoneNumber)
    }

    callback.invoke()
}

private fun Context.launchDialerIntent(phoneNumber: String?) {
    try {
        Intent(Intent.ACTION_DIAL).apply {
            if (!phoneNumber.isNullOrEmpty()) {
                putExtra("phone_number", phoneNumber)
                putExtra(Intent.EXTRA_TEXT, phoneNumber)
            }
            startActivity(this)
        }
    } catch (e: ActivityNotFoundException) {
        e.printStackTrace()
    }
}

fun Context.openMessage(message: String = "", callback: () -> Unit) {
    val callerIdSDKApplication = try {
        this.applicationContext as? CallerIdSDKApplication
    } catch (_: Exception) {
        null
    }
    try {
        if (CallerIdSDK.isHostDefaultSmsApp()) {
            val openClassForDefaultApp = callerIdSDKApplication?.openClassForDefaultApp
            when {
                openClassForDefaultApp != null -> {
                    Intent(this@openMessage, openClassForDefaultApp.provide()).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        if (message.isNotEmpty()) {
                            putExtra("sms_body", message)
                            putExtra(Intent.EXTRA_TEXT, message)
                        }
                        putExtra("isFromCallerId", true)
                        startActivity(this)
                    }
                    callback.invoke()
                }

                else -> {
                    val intent = Intent(Intent.ACTION_MAIN).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        addCategory(Intent.CATEGORY_APP_MESSAGING)
                    }
                    if (message.isNotEmpty()) {
                        intent.putExtra("sms_body", message)
                    }
                    startActivity(intent)
                    callback.invoke()
                }
            }
        } else {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                type = "vnd.android-dir/mms-sms"
            }
            if (message.isNotEmpty()) {
                intent.putExtra("sms_body", message)
            }
            startActivity(intent)
            callback.invoke()
        }
    } catch (_: ActivityNotFoundException) {
        try {
            val intent = Intent(Intent.ACTION_MAIN).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                addCategory(Intent.CATEGORY_APP_MESSAGING)
            }
            if (message.isNotEmpty()) {
                intent.putExtra("sms_body", message)
            }
            startActivity(intent)
            callback.invoke()
        } catch (inner: ActivityNotFoundException) {
            inner.printStackTrace()
        }
    } catch (e: Exception) {
        e.printStackTrace()
        try {
            val intent = Intent(Intent.ACTION_MAIN).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                addCategory(Intent.CATEGORY_APP_MESSAGING)
            }
            if (message.isNotEmpty()) {
                intent.putExtra("sms_body", message)
            }
            startActivity(intent)
            callback.invoke()
        } catch (inner: Exception) {
            inner.printStackTrace()
        }
    }
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
}

fun Context.isCIDPermissionAllowed(): Boolean {
    return CallerIdUtils.isPhoneStatePermissionGranted(this) && isScreenOverlayEnabled(this)
}

fun Context.getContactNameFromNumber(phoneNumber: String): String? {
    if (!CallerIdUtils.isReadContactPermissionGranted(this)) return null

    val uri = Uri.withAppendedPath(
        ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
        Uri.encode(phoneNumber)
    )

    contentResolver.query(
        uri,
        arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME),
        null,
        null,
        null
    )?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME)
        if (nameIndex != -1 && cursor.moveToFirst()) {
            return cursor.getString(nameIndex)
        }
    }
    return null
}

