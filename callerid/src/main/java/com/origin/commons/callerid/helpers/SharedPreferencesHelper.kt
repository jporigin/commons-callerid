package com.origin.commons.callerid.helpers

import android.content.Context
import android.content.SharedPreferences
import kotlin.toString

class SharedPreferencesHelper(private val context: Context) {

    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }


    var selectedAppTheme: Int
        get() = sharedPreferences.getInt(SELECTED_APP_THEME, 0)
        set(getSelectedAppTheme) = sharedPreferences.edit().putInt(SELECTED_APP_THEME, getSelectedAppTheme).apply()


    // Ads
    var adsRefreshIndex: Int
        get() = sharedPreferences.getInt(ADS_REFRESH_INDEX, -1)
        set(value) = sharedPreferences.edit().putInt(ADS_REFRESH_INDEX, value).apply()
    var adsRefreshType: String
        get() = sharedPreferences.getString(ADS_REFRESH_TYPE, "").toString()
        set(value) = sharedPreferences.edit().putString(ADS_REFRESH_TYPE, value).apply()
    var nativeBigIDsList: String
        get() = sharedPreferences.getString(NATIVE_BIG_IDS, "").toString()
        set(value) = sharedPreferences.edit().putString(NATIVE_BIG_IDS, value).apply()
    var nativeSmallIDsList: String
        get() = sharedPreferences.getString(NATIVE_SMALL_IDS, "").toString()
        set(value) = sharedPreferences.edit().putString(NATIVE_SMALL_IDS, value).apply()
    var bannerIDsList: String
        get() = sharedPreferences.getString(BANNER_IDS, "").toString()
        set(value) = sharedPreferences.edit().putString(BANNER_IDS, value).apply()

    var isPurchased: Boolean
        get() = sharedPreferences.getBoolean(IS_PURCHASED, false)
        set(value) = sharedPreferences.edit().putBoolean(IS_PURCHASED, value).apply()
    var isSkipCallerScreen: Boolean
        get() = sharedPreferences.getBoolean(SKIP_CALLER_SCREEN, false)
        set(value) = sharedPreferences.edit().putBoolean(SKIP_CALLER_SCREEN, value).apply()

    // Ads
    var savedReminderIds: String
        get() = sharedPreferences.getString(SAVED_REMINDER_IDS, "").toString()
        set(idString) = sharedPreferences.edit().putString(SAVED_REMINDER_IDS, idString).apply()

    var isMissedCallFeatureEnable: Boolean
        get() = getBooleanPref(context, IS_MISSED_CALL_FEATURE_ENABLE, true)
        set(value) = setBooleanPref(context, IS_MISSED_CALL_FEATURE_ENABLE, value)

    var isCompleteCallFeatureEnable: Boolean
        get() = getBooleanPref(context, IS_COMPLETE_CALL_FEATURE_ENABLE, true)
        set(value) = setBooleanPref(context, IS_COMPLETE_CALL_FEATURE_ENABLE, value)

    var isNoAnswerFeatureEnable: Boolean
        get() = getBooleanPref(context, IS_NO_ANSWER_FEATURE_ENABLE, true)
        set(value) = setBooleanPref(context, IS_NO_ANSWER_FEATURE_ENABLE, value)


    //
    var wicPosition: Int
        get() = getIntPref(context, "wicPosition", 300)
        set(value) = setIntPref(context, "wicPosition", value)

    var wicIsVertical: Boolean
        get() = getBooleanPref(context, "wic_is_vertical", false)
        set(value) = setBooleanPref(context, "wic_is_vertical", value)

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
            pref.edit().putInt(key, value).apply()
        }

        fun getStringPref(context: Context, key: String): String {
            val pref: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            return pref.getString(key, DEFAULT_STRING_VALUE) ?: DEFAULT_STRING_VALUE
        }

        fun setStringPref(context: Context, key: String, value: String) {
            val pref: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            pref.edit().putString(key, value).apply()
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
            pref.edit().putBoolean(key, value).apply()
        }

        fun getFloatPref(context: Context, key: String, value: Float): Float {
            val pref: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            return pref.getFloat(key, value)
        }

        fun setFloatPref(context: Context, key: String, value: Float) {
            val pref: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            pref.edit().putFloat(key, value).apply()
        }

    }

}


