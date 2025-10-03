package com.dev.notificationapp.domain.notification

import com.dev.notificationapp.domain.notification.application.dto.request.ReservationServiceRequest
import com.dev.notificationapp.domain.notification.enums.NotificationAttemptResult.SUCCESS
import com.dev.notificationapp.domain.user.User
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class NotificationAttemptTest {

    @Test
    fun createNotificationAttempt() {
        val user = User(null, "user1", "01012345678")
        val request = ReservationServiceRequest("제목1", "내용1", LocalDateTime.now())
        val notification = Notification.register(user, "idempotency-key1", request)

        val notificationAttempt = NotificationAttempt.create(notification, 1, SUCCESS)

        assertThat(notificationAttempt.notification).isNotNull
        assertThat(notificationAttempt.attemptNo).isEqualTo(1)
        assertThat(notificationAttempt.result).isEqualTo(SUCCESS)
    }
}