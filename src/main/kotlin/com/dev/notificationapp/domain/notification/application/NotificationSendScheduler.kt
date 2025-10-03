package com.dev.notificationapp.domain.notification.application

import com.dev.notificationapp.domain.notification.application.dto.request.SendNotificationRequest
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime

private val log = KotlinLogging.logger {}

@Component
class NotificationSendScheduler(
    private val notificationService: NotificationService,
    private val notificationSendService: NotificationSendService
) {

    @Scheduled(fixedRate = 5_000L)
    fun sendNotifications() {
        log.info { "알림 발송 스케줄러 실행 - ${LocalDateTime.now()}" }
        val reservedNotifications = notificationService.getReservedNotifications()

        for (reservedNotification in reservedNotifications) {
            val request = SendNotificationRequest.of(reservedNotification)
            val response = notificationSendService.externalSendNotificationApiCall(request)

            if (response.result == "SUCCESS") {
                notificationService.sendNotificationSuccess(reservedNotification.id!!)
            } else {
                notificationService.sendNotificationFailure(reservedNotification.id!!)
            }
        }
    }
}