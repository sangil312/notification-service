package com.dev.notificationapp.domain.notification.infrastructure

import com.dev.notificationapp.domain.notification.application.NotificationSendService
import com.dev.notificationapp.domain.notification.application.dto.request.SendNotificationRequest
import com.dev.notificationapp.domain.notification.infrastructure.dto.response.SendNotificationApiResponse
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import io.github.oshai.kotlinlogging.KotlinLogging

private val log = KotlinLogging.logger {}

@Component
class NotificationSendServiceImpl(
    private val webClient: WebClient
) : NotificationSendService {

    override fun externalSendNotificationApiCall(
        request: SendNotificationRequest
    ): SendNotificationApiResponse {
        try {
            val response = webClient.post()
                .uri("http://localhost:8010/notifications/send")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(SendNotificationApiResponse::class.java)
                .block()!!

            return response
        } catch (e: Exception) {
            log.error { "알림 발송 API 호출 실패 - http://localhost:8010/notifications/send - ${e.cause}" }
            return SendNotificationApiResponse("FAILURE", "예외 발생")
        }
    }

}