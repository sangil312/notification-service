package com.dev.notificationapp.domain.notification.application.dto.response

import com.dev.notificationapp.domain.notification.Notification

data class NotificationHistoryResponse(
    val notificationId: Long,
    val status: String,
    val retryCount: Int,
    val title: String,
    val reservedTime: String,
    val retryTime: String,
    val acceptTime: String
) {
    companion object {
        fun of(notification: Notification): NotificationHistoryResponse {
            return NotificationHistoryResponse(
                notificationId = notification.id!!,
                status = notification.status.name,
                retryCount = notification.retryCount,
                title = notification.title,
                reservedTime = notification.reservedAt.toString(),
                retryTime = notification.retryAt.toString(),
                acceptTime = notification.createdAt.toString()
            )
        }
    }
}
