package com.dev.notificationapp.domain.notification.application.dto.request

import com.dev.notificationapp.domain.notification.Notification

data class SendNotificationRequest(
    val phoneNumber: String,
    val title: String,
    val contents: String
) {

    companion object {
        fun of(notification: Notification): SendNotificationRequest {
            return SendNotificationRequest(
                notification.phoneNumber,
                notification.title,
                notification.contents
            )
        }
    }
}
