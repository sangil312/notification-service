package com.dev.notificationapp.domain.notification

import com.dev.notificationapp.common.exeption.NotificationCancelException
import com.dev.notificationapp.domain.notification.application.dto.request.ReservationServiceRequest
import com.dev.notificationapp.domain.notification.enums.NotificationStatus.*
import com.dev.notificationapp.domain.user.User
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class NotificationTest {
    private lateinit var notification: Notification

    @BeforeEach
    fun setUp() {
        val user = User(null, "user1", "01012345678")
        val request = ReservationServiceRequest("제목1", "내용1", LocalDateTime.now())
        notification = Notification.register(user, "idempotency-key1", request)
    }

    @Test
    fun registerNotification() {
        assertThat(notification.status).isEqualTo(RESERVED)
    }

    @Test
    fun checkCancelableStatus() {
        notification.checkCancelableStatus()
    }

    @Test
    fun checkCancelableStatusFail() {
        notification.changeStatus(SENT)

        assertThatThrownBy { notification.checkCancelableStatus() }
            .isInstanceOf(NotificationCancelException::class.java)
    }

    @Test
    fun cancelNotification() {
        notification.cancelNotification()

        assertThat(notification.status).isEqualTo(CANCELED)
    }

    @Test
    fun increaseAttemptCount() {
        notification.increaseAttemptCount()

        assertThat(notification.attemptCount).isEqualTo(1)
    }

    @Test
    fun handleNotificationFailure() {
        val retryAt = notification.retryAt

        notification.handleNotificationFailure()

        assertThat(notification.attemptCount).isEqualTo(1)
        assertThat(notification.retryAt).isEqualTo(retryAt.plusSeconds(10))
        assertThat(notification.status).isEqualTo(RESERVED)

        notification.handleNotificationFailure()

        assertThat(notification.attemptCount).isEqualTo(2)
        assertThat(notification.retryAt).isEqualTo(retryAt.plusSeconds(20))
        assertThat(notification.status).isEqualTo(RESERVED)

        notification.handleNotificationFailure()

        assertThat(notification.attemptCount).isEqualTo(3)
        assertThat(notification.retryAt).isEqualTo(retryAt.plusSeconds(20))
        assertThat(notification.status).isEqualTo(FAILED)
    }
}