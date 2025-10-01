package com.dev.notificationapp.domain.notification.application.dto.request

import java.time.LocalDateTime

data class ReservationServiceRequest(
    val title: String,
    val contents: String,
    val reserveTime: LocalDateTime
)
