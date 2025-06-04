package com.origin.commons.callerid.helpers

import android.content.Context
import android.content.SharedPreferences
import kotlin.toString

class SharedPreferencesHelper(private val context: Context) {

    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    var mAdFormat: String
        get() = sharedPreferences.getString(AD_FORMAT, "").toString()
        set(value) = sharedPreferences.edit().putString(AD_FORMAT, value).apply()
    var mAdUnitId: String
        get() = sharedPreferences.getString(AD_UNIT_ID, "").toString()
        set(value) = sharedPreferences.edit().putString(AD_UNIT_ID, value).apply()
    var mIsShowAdsShimmerView: Boolean
        get() = getBooleanPref(context, IS_SHOW_ADS_SHIMMER_VIEW, true)
        set(value) = setBooleanPref(context, IS_SHOW_ADS_SHIMMER_VIEW, value)


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


    companion object {
        fun newInstance(context: Context) = SharedPreferencesHelper(context)

        const val PREF_NAME = "CallEndFinalDemo"  // change while realise time
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


        const val AD_FORMAT = "AD_FORMAT"
        const val AD_UNIT_ID = "AD_UNIT_ID"
        const val IS_SHOW_ADS_SHIMMER_VIEW = "IS_SHOW_ADS_SHIMMER_VIEW"

        // Getters and setters for different types of preferences
        fun getIntPref(context: Context, key: String): Int {
            val pref: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            return pref.getInt(key, DEFAULT_INT_VALUE)
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

    }

}


