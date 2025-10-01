package com.dev.notificationapp.domain.notification.application

import com.dev.notificationapp.IntegrationTestSupport
import com.dev.notificationapp.common.exeption.DuplicateException
import com.dev.notificationapp.common.exeption.InvalidTimeException
import com.dev.notificationapp.domain.notification.Notification
import com.dev.notificationapp.domain.notification.NotificationRepository
import com.dev.notificationapp.domain.notification.application.dto.request.ReservationServiceRequest
import com.dev.notificationapp.domain.notification.enums.NotificationStatus
import com.dev.notificationapp.domain.notification.enums.NotificationStatus.RESERVED
import com.dev.notificationapp.domain.user.User
import com.dev.notificationapp.domain.user.UserRepository
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Transactional
class NotificationServiceTest(
    @Autowired val notificationService: NotificationService,
    @Autowired val userRepository: UserRepository,
    @Autowired val notificationRepository: NotificationRepository,
) : IntegrationTestSupport() {
    @DisplayName("알림 발송을 예약한다.")
    @Test
    fun reserveNotification() {
        //given
        val user = User(null, "user1", "01012345678")
        val idempotencyKey = "idempotency-key"
        val reserveTime = LocalDateTime.now().plusHours(1)
        val request = ReservationServiceRequest("알림 제목", "알림 내용", reserveTime)
        userRepository.save(user)

        //when
        val response = notificationService.reserveNotification(user.id!!, idempotencyKey, request)

        //then
        assertThat(response.notificationId).isNotNull()
        assertThat(response).extracting("status", "success", "reservedTime")
            .containsExactlyInAnyOrder(RESERVED.name, true, reserveTime.toString())
    }

    @DisplayName("동일한 알림 발송 예약 요청 시 예외 응답을 반환한다.")
    @Test
    fun reserveNotificationWithDuplicateRequest() {
        //given
        val user = User(null, "user1", "01012345678")
        val idempotencyKey = "idempotency-key"
        val reserveTime = LocalDateTime.now().plusHours(1)
        val request = ReservationServiceRequest("알림 제목", "알림 내용", reserveTime)
        userRepository.save(user)

        //when //then
        assertThatThrownBy { notificationService.reserveNotification(user.id!!, idempotencyKey, request) }
            .isInstanceOf(DuplicateException::class.java)
    }

    @DisplayName("알림 발송 예약 요청 시 예약 가능 시간을 초과하면 예외 응답을 반환한다.")
    @Test
    fun reserveNotificationWithTimeLimit() {
        //given
        val user = User(null, "user1", "01012345678")
        val idempotencyKey = "idempotency-key"
        val reserveTime = LocalDateTime.now().plusMinutes(121)
        val request = ReservationServiceRequest("알림 제목", "알림 내용", reserveTime)
        userRepository.save(user)

        //when //then
        assertThatThrownBy { notificationService.reserveNotification(user.id!!, idempotencyKey, request) }
            .isInstanceOf(InvalidTimeException::class.java)
    }

    @DisplayName("알림 발송 예약 내역을 조회한다.")
    @Test
    fun getNotifications() {
        //given
        val user = User(null, "user1", "01012345678")
        userRepository.save(user)

        val request = ReservationServiceRequest("알림 제목1", "알림 내용1", LocalDateTime.now())
        val notification = Notification.create(user, "idempotency-key1", request)
        notificationRepository.save(notification)

        val pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"))

        //when
        val response = notificationService.getNotifications(user.id!!, pageable)

        //then
        assertThat(response.content).hasSize(1)
        assertThat(response)
            .extracting("notificationId", "status", "retryCount", "title", "reservedTime", "acceptTime")
            .containsExactlyInAnyOrder(
                tuple(notification.id, "RESERVED", 0, "알림 제목1", request.reserveTime.toString(), notification.createdAt.toString()),
            )
    }
}