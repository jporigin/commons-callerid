package com.origin.commons.callerid

import android.app.Activity
import android.app.Application
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.MobileAds
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.gson.Gson
import com.origin.commons.callerid.extensions.logEventE
import com.origin.commons.callerid.extensions.prefsHelper
import com.origin.commons.callerid.extensions.registerCallReceiver
import com.origin.commons.callerid.helpers.NotificationConfig
import com.origin.commons.callerid.model.ThemeConfig

abstract class CallerIdSDKApplication : Application() {

    /**
     * Callback function invoked when the caller theme changes.
     * Implement this function to handle theme changes in your application.
     *
     * @param themeConfig A [ThemeConfig] object containing the new theme configuration.
     *                    This object encapsulates details about the selected theme,
     *                    such as whether it's light, dark, or system default, and
     *                    potentially other theme-related properties.
     */
    abstract fun onCallerThemeChanged(themeConfig: ThemeConfig)

    /**
     * Changes the theme of the caller ID screen.
     *
     * @param themeConfig The [ThemeConfig] to apply.
     *                    Use `ThemeConfig.SYSTEM` for the system theme.
     *                    Use `ThemeConfig.LIGHT` for the light theme.
     *                    Use `ThemeConfig.DARK` for the dark theme.
     */
    fun changeCallerTheme(themeConfig: ThemeConfig) {
        prefsHelper.themeConfig = themeConfig
    }


    /**
     * Controls the visibility of theme settings within the Caller ID SDK.
     * This function allows the host application to show or hide the theme customization options
     * provided by the SDK, typically within a settings screen.
     * Default value is `true`, meaning the theme settings will be visible.
     *
     * @param isVisible `true` to make the theme settings visible, `false` to hide them.
     */
    fun showThemeSettings(isVisible: Boolean) {
        prefsHelper.showThemeSettings = isVisible
    }


    /**
     * Controls whether only the Caller ID screen is shown, potentially bypassing other
     * popView or features within the SDK's call flow.
     *
     * @param isVisible Set to `true` to show only the Caller ID screen, `false` otherwise.
     *                  The default behavior when this is not explicitly set depends on the SDK's
     *                  internal logic.
     */
    fun showOnlyCallerIdScreen(isVisible: Boolean) {
        prefsHelper.showOnlyCallerIdScreen = isVisible
    }

    /**
     * Updates the preference indicating whether the overlay permission denied notification
     * should be shown.
     *
     * This function is used to control the visibility of a notification or UI element that
     * informs the user that the "Display over other apps" (overlay) permission has been denied,
     * which is crucial for the Caller ID functionality.
     *
     * @param isVisible `true` to indicate that the "overlay denied" notification should be visible,
     *                  `false` to hide it (e.g., after the user has granted the permission or
     *                  dismissed the notification).
     */
    fun notifyOverlayDenied(isVisible: Boolean) {
        prefsHelper.notifyOverlayDenied = isVisible
    }

    /**
     * Sets up the ad IDs for different ad types (native big, native small, banner).
     * This function stores the provided ad ID lists in shared preferences.
     * It also determines the `adsRefreshType` based on which lists are provided.
     * If the `adsRefreshType` changes, it resets `adsRefreshIndex`.
     *
     * @param nativeBigIds A list of ad unit IDs for big native ads. Defaults to an empty list.
     * @param nativeSmallIds A list of ad unit IDs for small native ads. Defaults to an empty list.
     * @param bannerIds A list of ad unit IDs for banner ads. Defaults to an empty list.
     */
    fun setUpAdsIDs(nativeBigIds: List<String> = emptyList(), nativeSmallIds: List<String> = emptyList(), bannerIds: List<String> = emptyList()) {
        val newAdsRefreshType = buildString {
            if (nativeBigIds.isNotEmpty()) append("1")
            if (nativeSmallIds.isNotEmpty()) append("2")
            if (bannerIds.isNotEmpty()) append("3")
        }
        prefsHelper.apply {
            if (this.adsRefreshType != newAdsRefreshType) {
                this.adsRefreshIndex = -1
                this.adsRefreshType = newAdsRefreshType
            }
            if (nativeBigIds.isNotEmpty()) {
                this.nativeBigIDsList = Gson().toJson(nativeBigIds)
            }
            if (nativeSmallIds.isNotEmpty()) {
                this.nativeSmallIDsList = Gson().toJson(nativeSmallIds)
            }
            if (bannerIds.isNotEmpty()) {
                this.bannerIDsList = Gson().toJson(bannerIds)
            }
        }
    }

    /**
     * Sets up the ad IDs for different ad types.
     *
     * @param adsRefreshType A string indicating the types of ads to be refreshed.
     *                       Defaults to "123", which means refresh native big, native small, and banner ads.
     *                       "1" means refresh only native big ads.
     *                       "2" means refresh only native small ads.
     *                       "3" means refresh only banner ads.
     *                       "12" means refresh native big and native small ads.
     *                       "13" means refresh native big and banner ads.
     *                       "23" means refresh native small and banner ads.
     * @param nativeBigIds A list of strings representing the IDs for native big ads. Defaults to an empty list.
     * @param nativeSmallIds A list of strings representing the IDs for native small ads. Defaults to an empty list.
     * @param bannerIds A list of strings representing the IDs for banner ads. Defaults to an empty list.
     */
    fun setUpAdsIDs(adsRefreshType: String = "123", nativeBigIds: List<String> = emptyList(), nativeSmallIds: List<String> = emptyList(), bannerIds: List<String> = emptyList()) {
        prefsHelper.apply {
            if (this.adsRefreshType != adsRefreshType) {
                this.adsRefreshIndex = -1
                this.adsRefreshType = adsRefreshType
            }
            if (nativeBigIds.isNotEmpty()) {
                this.nativeBigIDsList = Gson().toJson(nativeBigIds)
            }
            if (nativeSmallIds.isNotEmpty()) {
                this.nativeSmallIDsList = Gson().toJson(nativeSmallIds)
            }
            if (bannerIds.isNotEmpty()) {
                this.bannerIDsList = Gson().toJson(bannerIds)
            }
        }
    }

    /**
     * Sets up the purchase status and whether to skip the caller screen.
     *
     * @param isPurchased A boolean indicating whether the user has purchased the app. Defaults to false.
     * @param skipCallerScreen A boolean indicating whether to skip the caller screen. Defaults to false.
     */
    fun setUpPurchase(isPurchased: Boolean = false, skipCallerScreen: Boolean = false) {
        prefsHelper.apply {
            this.isPurchased = isPurchased
            this.isSkipCallerScreen = skipCallerScreen
        }
    }

    var appLogo: Int? = null
    var appLogoIcon: Int? = null

    /**
     * Sets up the application logo and icon resources.
     *
     * @param logo The resource ID of the application logo.
     * @param logoIcon The resource ID of the application logo icon.
     */
    fun setUp(logo: Int, logoIcon: Int) {
        appLogo = logo
        appLogoIcon = logoIcon
    }

    var notificationConfig: NotificationConfig? = null

    /**
     * Sets the notification configuration for the Caller ID SDK.
     * This allows customization of how notifications related to incoming calls or other
     * SDK events are presented to the user.
     *
     * @param notificationConfig The [NotificationConfig] object containing the desired
     *                           notification settings (e.g., icons, channels, texts).
     */
    fun setUpNotificationConfig(notificationConfig: NotificationConfig) {
        this.notificationConfig = notificationConfig
    }

    fun interface ActivityClassProvider {
        fun provide(): Class<*>
    }

    fun interface FragmentClassProvider {
        fun provide(): Fragment
    }

    /**
     *  An [ActivityClassProvider] that provides the class of the Activity to be opened when the user interacts with elements like the logo or notifications, intending to bring the app to the foreground if it's already running.
     *  This is typically the main or home activity of your application.
     */
    var openClass1: ActivityClassProvider? = null

    /**
     *  An [ActivityClassProvider] instance that returns the [Class] of the [Activity] to be opened.
     *  This class is used for the high priority notification.
     */
    var openClass2High: ActivityClassProvider? = null

    /**
     *  An optional custom [Fragment] that can be provided to replace the default home screen fragment within the SDK's UI.
     *  This allows for a more integrated and branded user experience.
     *  If `null`, the SDK's default home fragment will be used.
     *  The provided [FragmentClassProvider] should return an instance of your custom fragment.
     */
    var customHomeFragment: FragmentClassProvider? = null


    /**
     * An optional [ActivityClassProvider] that provides the class of a custom [Activity] to be opened
     * when the user navigates to the caller ID settings screen from within the SDK.
     * If `null`, the SDK's default caller ID settings screen will be used.
     * This allows developers to integrate their own settings UI seamlessly with the SDK.
     */
    var customCallerSetting: ActivityClassProvider? = null

    /**
     * An [ActivityClassProvider] that provides the class of the Activity to be opened when the
     * "Set as Default App" button is clicked within the SDK's UI (e.g., on the home screen or settings).
     * This is typically the activity in your application that handles the intent for becoming the default
     * phone, SMS, or other relevant application type.
     * If `null`, the SDK might attempt a default action or the button might be hidden.
     */
    var openClassForDefaultApp: ActivityClassProvider? = null

    override fun onCreate() {
        super.onCreate()
        initSDK()
    }

    /**
     * Initializes the SDK.
     * This function performs the following actions:
     * 1. Initializes MobileAds on a background thread.
     * 2. Registers the call receiver.
     * 3. Initializes Firebase Analytics.
     * 4. Logs an event indicating that the SDK has been initialized.
     */
    private fun initSDK() {
        try {
            Thread {
                MobileAds.initialize(this)
            }.start()
        } catch (_: Exception) {
        }
        registerCallReceiver()
        mFirebaseAnalytics = Firebase.analytics
        logEventE("Initialized_OGCallerIdSDK")
        return
    }


    /**
     * Checks if the missed call feature is enabled.
     *
     * @return `true` if the missed call feature is enabled, `false` otherwise.
     */
    fun getMissedCallFeatureEnable(): Boolean {
        return prefsHelper.isMissedCallFeatureEnable
    }

    fun setMissedCallFeatureEnable(value: Boolean) {
        prefsHelper.isMissedCallFeatureEnable = value
    }


    /**
     * Retrieves the current status of the "Complete Call" feature.
     * This feature likely relates to functionalities triggered after a call has ended.
     *
     * @return `true` if the "Complete Call" feature is enabled, `false` otherwise.
     */
    fun getCompleteCallFeatureEnable(): Boolean {
        return prefsHelper.isCompleteCallFeatureEnable
    }

    fun setCompleteCallFeatureEnable(value: Boolean) {
        prefsHelper.isCompleteCallFeatureEnable = value
    }


    /**
     * Checks if the "No Answer" feature is enabled.
     * This feature likely relates to handling calls that are not answered by the user.
     *
     * @return `true` if the "No Answer" feature is enabled, `false` otherwise.
     */
    fun getNoAnswerFeatureEnable(): Boolean {
        return prefsHelper.isNoAnswerFeatureEnable
    }

    fun setNoAnswerFeatureEnable(value: Boolean) {
        prefsHelper.isNoAnswerFeatureEnable = value
    }


    companion object {
        @Volatile
        var mFirebaseAnalytics: FirebaseAnalytics? = null

        fun getFirebaseAnalytics(): FirebaseAnalytics {
            return mFirebaseAnalytics ?: synchronized(this) {
                mFirebaseAnalytics ?: Firebase.analytics.also { mFirebaseAnalytics = it }
            }
        }
    }
}