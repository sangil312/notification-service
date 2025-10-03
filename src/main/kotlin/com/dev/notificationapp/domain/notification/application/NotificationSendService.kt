package com.dev.notificationapp.domain.notification.application

import com.dev.notificationapp.domain.notification.application.dto.request.SendNotificationRequest
import com.dev.notificationapp.domain.notification.infrastructure.dto.response.SendNotificationApiResponse

interface NotificationSendService {
    fun externalSendNotificationApiCall(
        request: SendNotificationRequest
    ) : SendNotificationApiResponse
}