package com.dev.notificationapp.domain.notification.infrastructure

import com.dev.notificationapp.IntegrationTestSupport
import com.dev.notificationapp.domain.notification.Notification
import com.dev.notificationapp.domain.notification.NotificationRepository
import com.dev.notificationapp.domain.notification.application.dto.request.ReservationServiceRequest
import com.dev.notificationapp.domain.notification.enums.NotificationStatus
import com.dev.notificationapp.domain.user.User
import com.dev.notificationapp.domain.user.UserRepository
import jakarta.transaction.Transactional
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import java.time.LocalDateTime

@Transactional
class NotificationRepositoryImplTest(
    @Autowired val notificationRepository: NotificationRepository,
    @Autowired val userRepository: UserRepository
) : IntegrationTestSupport() {

    @DisplayName("정렬 가능 컬럼이 존재하지 않거나 null 이면 createdAt 를 내림차순으로 조회한다.")
    @Test
    fun findAllByUserId() {
        //given
        val user = User(null, "user1", "01012345678")
        userRepository.save(user)

        val now = LocalDateTime.now()
        val notification1 = createNotification(
            user, "idempotency-key1", "알림 제목1", "알림 내용1", now.plusMinutes(1))
        val notification2 = createNotification(
            user, "idempotency-key2", "알림 제목2", "알림 내용2", now.plusMinutes(2))
        val notification3 = createNotification(
            user, "idempotency-key3", "알림 제목3", "알림 내용3", now.plusMinutes(3))
        notificationRepository.saveAll(listOf(notification1, notification2, notification3))

        val pageable = PageRequest.of(0, 10)

        //when
        val notifications = notificationRepository.findAllByUserId(user.id!!, NotificationStatus.of(null), pageable)

        //then
        assertThat(notifications).hasSize(3)
            .extracting(
                "idempotencyKey", "title", "contents", "reservedAt", "retryAt")
            .containsExactly(
                tuple("idempotency-key3", "알림 제목3", "알림 내용3", notification3.reservedAt, notification3.reservedAt),
                tuple("idempotency-key2", "알림 제목2", "알림 내용2", notification2.reservedAt, notification2.reservedAt),
                tuple("idempotency-key1", "알림 제목1", "알림 내용1", notification1.reservedAt, notification1.reservedAt)
            )
    }

    @DisplayName("예약된 시간의 오름차순으로 알림 발송 예약을 조회한다.")
    @Test
    fun findAllByUserIdOrderByReservedAtASC() {
        //given
        val user = User(null, "user1", "01012345678")
        userRepository.save(user)

        val now = LocalDateTime.now()
        val notification1 = createNotification(
            user, "idempotency-key1", "알림 제목1", "알림 내용1", now.plusMinutes(1))
        val notification2 = createNotification(
            user, "idempotency-key2", "알림 제목2", "알림 내용2", now.plusMinutes(2))
        val notification3 = createNotification(
            user, "idempotency-key3", "알림 제목3", "알림 내용3", now.plusMinutes(3))
        notificationRepository.saveAll(listOf(notification1, notification2, notification3))

        val pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "reservedAt"))

        //when
        val notifications = notificationRepository.findAllByUserId(user.id!!, NotificationStatus.of(null), pageable)

        //then
        assertThat(notifications).hasSize(3)
            .extracting(
                "idempotencyKey", "title", "contents", "reservedAt", "retryAt")
            .containsExactly(
                tuple("idempotency-key1", "알림 제목1", "알림 내용1", notification1.reservedAt, notification1.reservedAt),
                tuple("idempotency-key2", "알림 제목2", "알림 내용2", notification2.reservedAt, notification2.reservedAt),
                tuple("idempotency-key3", "알림 제목3", "알림 내용3", notification3.reservedAt, notification3.reservedAt)
            )
    }

    @DisplayName("알림 발송 예약 상태로 조회가 가능하고, null 이거나 올바른 상태값이 아닐경우 상태 조건없이 조회한다.")
    @Test
    fun findAllByUserIdWithStatus() {
        //given
        val user = User(null, "user1", "01012345678")
        userRepository.save(user)

        val now = LocalDateTime.now()
        val notification1 = createNotification(
            user, "idempotency-key1", "알림 제목1", "알림 내용1", now.plusMinutes(1))
        val notification2 = createNotification(
            user, "idempotency-key2", "알림 제목2", "알림 내용2", now.plusMinutes(2))
        val notification3 = createNotification(
            user, "idempotency-key3", "알림 제목3", "알림 내용3", now.plusMinutes(3))
        notificationRepository.saveAll(listOf(notification1, notification2, notification3))

        val pageable = PageRequest.of(0, 10)

        //when
        val notifications1 = notificationRepository.findAllByUserId(user.id!!, NotificationStatus.of("CANCELED"), pageable)
        //then
        assertThat(notifications1).isEmpty()

        // when
        val notifications2 = notificationRepository.findAllByUserId(user.id!!, null, pageable)
        //then
        assertThat(notifications2).hasSize(3)
            .extracting(
                "idempotencyKey", "title", "contents", "reservedAt", "retryAt")
            .containsExactly(
                tuple("idempotency-key3", "알림 제목3", "알림 내용3", notification3.reservedAt, notification3.reservedAt),
                tuple("idempotency-key2", "알림 제목2", "알림 내용2", notification2.reservedAt, notification2.reservedAt),
                tuple("idempotency-key1", "알림 제목1", "알림 내용1", notification1.reservedAt, notification1.reservedAt)
            )
    }

    private fun createNotification(
        user: User,
        idempotencyKey: String,
        title: String,
        contents: String,
        reserveTime: LocalDateTime,
    ) : Notification {
        val request = ReservationServiceRequest(title, contents, reserveTime)
        return Notification.create(user, idempotencyKey, request)
    }
}