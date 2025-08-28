package com.origin.commons.callerid.helpers

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.origin.commons.callerid.model.PopViewType
import com.origin.commons.callerid.model.ThemeConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.shareIn

class SharedPreferencesHelper(private val context: Context) {

    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    var themeConfig: ThemeConfig
        get() = ThemeConfig.entries[getIntPref(context, SELECTED_APP_THEME, ThemeConfig.SYSTEM_THEME.value)]
        set(value) = setIntPref(context, SELECTED_APP_THEME, value.value)

    // Ads
    var adsRefreshIndex: Int
        get() = sharedPreferences.getInt(ADS_REFRESH_INDEX, -1)
        set(value) = sharedPreferences.edit { putInt(ADS_REFRESH_INDEX, value) }
    var adsRefreshType: String
        get() = sharedPreferences.getString(ADS_REFRESH_TYPE, "").toString()
        set(value) = sharedPreferences.edit { putString(ADS_REFRESH_TYPE, value) }
    var nativeBigIDsList: String
        get() = sharedPreferences.getString(NATIVE_BIG_IDS, "").toString()
        set(value) = sharedPreferences.edit { putString(NATIVE_BIG_IDS, value) }
    var nativeSmallIDsList: String
        get() = sharedPreferences.getString(NATIVE_SMALL_IDS, "").toString()
        set(value) = sharedPreferences.edit { putString(NATIVE_SMALL_IDS, value) }
    var bannerIDsList: String
        get() = sharedPreferences.getString(BANNER_IDS, "").toString()
        set(value) = sharedPreferences.edit { putString(BANNER_IDS, value) }

    var isPurchased: Boolean
        get() = sharedPreferences.getBoolean(IS_PURCHASED, false)
        set(value) = sharedPreferences.edit { putBoolean(IS_PURCHASED, value) }
    var isSkipCallerScreen: Boolean
        get() = sharedPreferences.getBoolean(SKIP_CALLER_SCREEN, false)
        set(value) = sharedPreferences.edit { putBoolean(SKIP_CALLER_SCREEN, value) }

    // Caller Setting
    var savedReminderIds: String
        get() = sharedPreferences.getString(SAVED_REMINDER_IDS, "").toString()
        set(idString) = sharedPreferences.edit { putString(SAVED_REMINDER_IDS, idString) }

    var isMissedCallFeatureEnable: Boolean
        get() = getBooleanPref(context, IS_MISSED_CALL_FEATURE_ENABLE, true)
        set(value) = setBooleanPref(context, IS_MISSED_CALL_FEATURE_ENABLE, value)

    var isCompleteCallFeatureEnable: Boolean
        get() = getBooleanPref(context, IS_COMPLETE_CALL_FEATURE_ENABLE, true)
        set(value) = setBooleanPref(context, IS_COMPLETE_CALL_FEATURE_ENABLE, value)

    var isNoAnswerFeatureEnable: Boolean
        get() = getBooleanPref(context, IS_NO_ANSWER_FEATURE_ENABLE, true)
        set(value) = setBooleanPref(context, IS_NO_ANSWER_FEATURE_ENABLE, value)

    var showThemeSettings: Boolean
        get() = getBooleanPref(context, IS_SHOW_THEME_SETTINGS, true)
        set(value) = setBooleanPref(context, IS_SHOW_THEME_SETTINGS, value)

    //
    var wicStandardPosition: Int
        get() = getIntPref(context, "wicStandardPosition", 300)
        set(value) = setIntPref(context, "wicStandardPosition", value)

    var wicClassicPosition: Int
        get() = getIntPref(context, "wicClassicPosition", 0)
        set(value) = setIntPref(context, "wicClassicPosition", value)

    var wicIsVertical: Boolean
        get() = getBooleanPref(context, "wicIsVertical", false)
        set(value) = setBooleanPref(context, "wicIsVertical", value)

    var popViewType: PopViewType
        get() = PopViewType.entries[getIntPref(context, "popViewType", PopViewType.STANDARD.value)]
        set(value) = setIntPref(context, "popViewType", value.value)

    var showStandardPopupOnClassicClose: Boolean
        get() = getBooleanPref(context, "showStandardPopupOnClassicClose", false)
        set(value) = setBooleanPref(context, "showStandardPopupOnClassicClose", value)

    var showOnlyCallerIdScreen: Boolean
        get() = getBooleanPref(context, "showOnlyCallerIdScreen", false)
        set(value) = setBooleanPref(context, "showOnlyCallerIdScreen", value)

    // OgCallerIdCallReceiver Helper
    var isIncomingCallRinging: Boolean
        get() = getBooleanPref(context, "isIncomingCallRinging", false)
        set(value) = setBooleanPref(context, "isIncomingCallRinging", value)

    var isOutgoingCallRinging: Boolean
        get() = getBooleanPref(context, "isOutgoingCallRinging", false)
        set(value) = setBooleanPref(context, "isOutgoingCallRinging", value)

    var isAnyIncomingCallWaiting: Boolean
        get() = getBooleanPref(context, "isAnyIncomingCallWaiting", false)
        set(value) = setBooleanPref(context, "isAnyIncomingCallWaiting", value)

    var isAnyOutgoingCallWaiting: Boolean
        get() = getBooleanPref(context, "isAnyOutgoingCallWaiting", false)
        set(value) = setBooleanPref(context, "isAnyOutgoingCallWaiting", value)

    var callPhoneNumber: String
        get() = getStringPref(context, "callPhoneNumber", "Unknown")
        set(value) = setStringPref(context, "callPhoneNumber", value)

    var callStartTime: Long
        get() = getLongPref(context, "callStartTime")
        set(value) = setLongPref(context, "callStartTime", value)

    var callType: String
        get() = getStringPref(context, "callType")
        set(value) = setStringPref(context, "callType", value)


    fun keyChangesFlow(scope: CoroutineScope): Flow<String> =
        callbackFlow {
            val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
                trySend(key ?: "")
            }

            sharedPreferences.registerOnSharedPreferenceChangeListener(listener)

            awaitClose {
                sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
            }
        }.shareIn(scope, started = SharingStarted.Lazily, replay = 0)

    companion object {
        fun newInstance(context: Context) = SharedPreferencesHelper(context)

        const val PREF_NAME = "CallEndFinalDemo"  // change while realise time


        private const val SELECTED_APP_THEME = "call_selected_theme"

        private const val DEFAULT_INT_VALUE = -1
        private const val DEFAULT_STRING_VALUE = ""
        private const val DEFAULT_BOOLEAN_VALUE = false

        const val PERMISSIONS_GRANTED = "PERMISSIONS_GRANTED"
        const val SCREEN_OVERLAY_PERMISSION_GRANTED = "SCREEN_OVERLAY_PERMISSION_GRANTED"
        const val PHONE_PERMISSION_REQUEST_ASKED = "PHONE_PERMISSION_REQUEST_ASKED"
        const val NOTI_PERMISSION_REQUEST_ASKED = "NOTI_PERMISSION_REQUEST_ASKED"

        const val IS_MISSED_CALL_FEATURE_ENABLE = "IS_MISSED_CALL_FEATURE_ENABLE"
        const val IS_COMPLETE_CALL_FEATURE_ENABLE = "IS_COMPLETE_CALL_FEATURE_ENABLE"
        const val IS_NO_ANSWER_FEATURE_ENABLE = "IS_NO_ANSWER_FEATURE_ENABLE"

        const val IS_SHOW_THEME_SETTINGS = "IS_SHOW_THEME_SETTINGS"

        const val SAVED_REMINDER_IDS = "SAVED_REMINDER_IDS"


        const val ADS_REFRESH_INDEX = "ADS_REFRESH_INDEX"
        const val ADS_REFRESH_TYPE = "ADS_REFRESH_TYPE"
        const val NATIVE_BIG_IDS = "NATIVE_BIG_IDS"
        const val NATIVE_SMALL_IDS = "NATIVE_SMALL_IDS"
        const val BANNER_IDS = "BANNER_IDS"
        const val IS_PURCHASED = "IS_PURCHASED"
        const val SKIP_CALLER_SCREEN = "SKIP_CALLER_SCREEN"

        // Getters and setters for different types of preferences
        fun getIntPref(context: Context, key: String, value: Int): Int {
            val pref: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            return pref.getInt(key, value)
        }

        fun setIntPref(context: Context, key: String, value: Int) {
            val pref: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            pref.edit { putInt(key, value) }
        }

        fun getStringPref(context: Context, key: String, value: String = DEFAULT_STRING_VALUE): String {
            val pref: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            return pref.getString(key, value) ?: value
        }

        fun setStringPref(context: Context, key: String, value: String) {
            val pref: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            pref.edit { putString(key, value) }
        }

        fun getBooleanPref(context: Context, key: String): Boolean {
            val pref: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            return pref.getBoolean(key, DEFAULT_BOOLEAN_VALUE)
        }

        fun getBooleanPref(context: Context, key: String, value: Boolean): Boolean {
            val pref: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            return pref.getBoolean(key, value)
        }

        fun setBooleanPref(context: Context, key: String, value: Boolean) {
            val pref: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            pref.edit { putBoolean(key, value) }
        }

        fun getFloatPref(context: Context, key: String, value: Float): Float {
            val pref: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            return pref.getFloat(key, value)
        }

        fun setFloatPref(context: Context, key: String, value: Float) {
            val pref: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            pref.edit { putFloat(key, value) }
        }

        fun getLongPref(context: Context, key: String): Long {
            val pref: SharedPreferences =
                context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            return pref.getLong(key, 0)
        }

        fun setLongPref(context: Context, key: String, value: Long) {
            val pref: SharedPreferences =
                context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            pref.edit { putLong(key, value) }
        }

    }

}


