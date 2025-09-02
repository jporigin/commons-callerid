package com.origin.commons.callerid.helpers

import com.origin.commons.callerid.utils.DEFAULT_NOTIFICATION_DESCRIPTION
import com.origin.commons.callerid.utils.DEFAULT_NOTIFICATION_TITLE

data class NotificationConfig(
    val notificationId: Int = 1,
    val title: String = DEFAULT_NOTIFICATION_TITLE,
    val description: String = DEFAULT_NOTIFICATION_DESCRIPTION,
    val smallIcon: Int? = null,
    val pendingClass: Class<*>? = null
)