package com.dev.notificationapp.domain.notification.application.dto.response

import com.dev.notificationapp.domain.notification.Notification

data class ReservationResponse(
    val notificationId : Long,
    val status: String,
    val success: Boolean,
    val reservedTime: String
) {
    companion object {
        fun of(notification: Notification): ReservationResponse {
            return ReservationResponse(
                notificationId = notification.id!!,
                status = notification.status.name,
                success = true,
                reservedTime = notification.reservedAt.toString()
            )
        }
    }
}
