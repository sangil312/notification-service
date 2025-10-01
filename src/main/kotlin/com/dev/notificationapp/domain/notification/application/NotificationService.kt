package com.dev.notificationapp.domain.notification.application

import com.dev.notificationapp.domain.notification.application.dto.request.ReservationServiceRequest
import com.dev.notificationapp.domain.notification.application.dto.response.NotificationHistoryResponse
import com.dev.notificationapp.domain.notification.application.dto.response.ReservationResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface NotificationService {
    fun reserveNotification(
        userId: Long,
        idempotencyKey: String,
        request: ReservationServiceRequest
    ) : ReservationResponse

    fun getNotifications(
        userId: Long,
        pageable: Pageable
    ) : Page<NotificationHistoryResponse>
}