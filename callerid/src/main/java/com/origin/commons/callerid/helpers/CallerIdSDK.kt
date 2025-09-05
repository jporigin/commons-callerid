package com.origin.commons.callerid.helpers

import android.Manifest
import android.app.role.RoleManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Telephony
import android.telecom.TelecomManager
import androidx.core.content.ContextCompat

object CallerIdSDK {

    private var appContext: Context? = null

    /**
     * Initializes the CallerIdSDK with the application context.
     * This method must be called before any other methods of the SDK are used.
     *
     * @param context The application context.
     */
    fun init(context: Context) {
        appContext = context.applicationContext
    }

    /**
     * Checks if the host application is the default SMS app.
     *
     * This function determines if the application that initialized the SDK is currently set as the
     * default SMS application on the device.
     *
     * For devices running Android Q (API level 29) and above, it uses the `RoleManager` to check
     * if the app holds the `ROLE_SMS`.
     * For devices running below Android Q, it uses `Telephony.Sms.getDefaultSmsPackage()` to
     * get the package name of the default SMS app and compares i
     * t with the host app's package name.
     *
     * @return `true` if the host application is the default SMS app, `false` otherwise.
     *         Returns `false` if the SDK has not been initialized (i.e., `appContext` is null).
     */
    fun isHostDefaultSmsApp(): Boolean {
        val context = appContext ?: return false
        val hostPackageName = context.packageName

        val hasAllPermissions =
            hasPermission(context, Manifest.permission.READ_SMS) &&
                    hasPermission(context, Manifest.permission.SEND_SMS) &&
                    hasPermission(context, Manifest.permission.READ_CONTACTS)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val roleManager = context.getSystemService(RoleManager::class.java)
            if (roleManager?.isRoleHeld(RoleManager.ROLE_SMS) == true) true else hasAllPermissions
        } else {
            if (Telephony.Sms.getDefaultSmsPackage(context) == hostPackageName) true else hasAllPermissions
        }
    }

    private fun hasPermission(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Checks if the host application is the default dialer app on the device.
     *
     * This function uses the `RoleManager` for Android Q (API level 29) and above to check if the
     * host app holds the `ROLE_DIALER`. For older versions, it uses the `TelecomManager` to
     * compare the host app's package name with the default dialer package.
     *
     * @return `true` if the host app is the default dialer, `false` otherwise.
     *         Returns `false` if the `appContext` is null.
     */
    fun isHostDefaultDialerApp(): Boolean {
        val context = appContext ?: return false
        val hostPackageName = context.packageName

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val roleManager = context.getSystemService(RoleManager::class.java)
            roleManager?.isRoleHeld(RoleManager.ROLE_DIALER) == true
        } else {
            val telecomManager = context.getSystemService(Context.TELECOM_SERVICE) as? TelecomManager
            telecomManager?.defaultDialerPackage == hostPackageName
        }
    }
}
