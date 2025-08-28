package com.origin.commons.callerid.sample.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.origin.commons.callerid.helpers.CallerIdUtils.isPhoneStatePermissionGranted
import com.origin.commons.callerid.helpers.CallerIdUtils.isScreenOverlayEnabled
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PermissionViewModel() : ViewModel() {

    private val _uiState = MutableStateFlow(PermissionUiState())
    val uiState: StateFlow<PermissionUiState> = _uiState

    fun checkPermissions(context: Context) {
        viewModelScope.launch {
            if (isPhoneStatePermissionGranted(context)) {
                _uiState.value = _uiState.value.copy(
                    step = 2,
                    title = "Screen Overlay Permission",
                    description = "Please allow screen overlay permission to proceed.",
                    btnText = "Allow Overlay",
                    permissionState = PermissionState.DISPLAY_OVER_APP_PERMISSION
                )
                if (isScreenOverlayEnabled(context)) {
                    completePermissions()
                }
            } else {
                requestRegularPermission()
            }
        }
    }

    private fun requestRegularPermission() {
        _uiState.value = _uiState.value.copy(
            step = 1,
            title = "Allow Permission",
            description = "Please allow the required permission to access full features of the application.",
            btnText = "Allow",
            permissionState = PermissionState.REGULAR_PERMISSION
        )
    }

    private fun completePermissions() {
        _uiState.value = _uiState.value.copy(
            step = 3,
            title = "Permissions Complete",
            description = "All required permissions are granted. You can now use the full features.",
            btnText = "Continue",
            permissionState = PermissionState.COMPLETE
        )
    }

    fun updatePermissionStatus(context: Context) {
        if (isPhoneStatePermissionGranted(context)) {
            checkPermissions(context)
        } else if (isPhoneStatePermissionGranted(context) && isScreenOverlayEnabled(context)) {
            completePermissions()
        }
    }

}

enum class PermissionState {
    REGULAR_PERMISSION,
    DISPLAY_OVER_APP_PERMISSION,
    COMPLETE
}

data class PermissionUiState(
    val step: Int = 1,
    val title: String = "Allow Permission",
    val description: String = "Please allow the required permission to access full features of the application",
    val btnText: String = "Allow",
    val permissionState: PermissionState = PermissionState.REGULAR_PERMISSION
)