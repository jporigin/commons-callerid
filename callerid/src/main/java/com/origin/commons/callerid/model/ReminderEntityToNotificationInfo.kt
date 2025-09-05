package com.origin.commons.callerid.model

import com.origin.commons.callerid.db.entity.ReminderEntity

fun ReminderEntity.toNotificationInfo(timeInMillis: Long) = NotificationInfo(notificationId = id.toString(), title = title.toString(), description = message.toString(), timeInMillis = timeInMillis)