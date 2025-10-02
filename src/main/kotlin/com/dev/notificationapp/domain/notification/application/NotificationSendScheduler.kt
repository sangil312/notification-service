package com.dev.notificationapp.domain.notification.application

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class NotificationSendScheduler() {

    @Scheduled(fixedRate = 3_000L)
    fun sendNotifications() {
        println("Sending notifications...")

        //TODO  pending, now <= retryAt
        // 500 "errorMessage": "에러 발생"
        // retryCount == maxRetryCount ? notification.status = FAILED
        // retryCount <= maxRetryCount ? notification.status = PENDING
        // notification_attempt 알림 ID, 실패, 재시도 횟수 저장
        // ------------
        // 200 "result": "SUCCESS"
        // notification.status = SENT
        // notification_attempt 알림 ID, 성공, 재시도 횟수 저장
    }
}