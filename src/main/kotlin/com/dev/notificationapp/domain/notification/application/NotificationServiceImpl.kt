package com.dev.notificationapp.domain.notification.application

import com.dev.notificationapp.common.exeption.DuplicateException
import com.dev.notificationapp.common.exeption.NotFoundException
import com.dev.notificationapp.common.exeption.InvalidTimeException
import com.dev.notificationapp.common.exeption.enums.ErrorCode.*
import com.dev.notificationapp.domain.notification.Notification
import com.dev.notificationapp.domain.notification.NotificationAttempt
import com.dev.notificationapp.domain.notification.NotificationAttemptRepository
import com.dev.notificationapp.domain.notification.NotificationRepository
import com.dev.notificationapp.domain.notification.application.dto.request.ReservationServiceRequest
import com.dev.notificationapp.domain.notification.application.dto.response.NotificationHistoryResponse
import com.dev.notificationapp.domain.notification.application.dto.response.ReservationResponse
import com.dev.notificationapp.domain.notification.enums.NotificationAttemptResult
import com.dev.notificationapp.domain.notification.enums.NotificationStatus
import com.dev.notificationapp.domain.user.UserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class NotificationServiceImpl(
    private val notificationRepository: NotificationRepository,
    private val notificationAttemptRepository: NotificationAttemptRepository,
    private val userRepository: UserRepository,
) : NotificationService {

    @Transactional
    override fun reserveNotification(
        userId: Long,
        idempotencyKey: String,
        request: ReservationServiceRequest
    ) : ReservationResponse {
        // 중복 요청 확인
        if (notificationRepository.existsByIdempotencyKey(idempotencyKey)) {
            throw DuplicateException(DUPLICATE_REQUEST)
        }

        val user = userRepository.findById(userId)
            .orElseThrow { throw NotFoundException(USER_NOT_FOUND) }

        notificationReserveTimeCheck(request.reserveTime)

        val notification = Notification.register(user, idempotencyKey, request)
        notificationRepository.save(notification)

        return ReservationResponse.of(notification)
    }

    private fun notificationReserveTimeCheck(reserveTime: LocalDateTime) {
        val now = LocalDateTime.now()

        if (reserveTime.isBefore(now)) {
            throw InvalidTimeException(RESERVATION_TIME_AFTER_NOW)
        }

        if (reserveTime.isAfter(now.plusHours(2))) {
            throw InvalidTimeException(RESERVATION_TIME_LIMIT)
        }
    }

    @Transactional(readOnly = true)
    override fun getNotifications(
        userId: Long,
        status: String?,
        pageable: Pageable
    ) : Page<NotificationHistoryResponse> {
        if (!userRepository.existsById(userId)) throw NotFoundException(USER_NOT_FOUND)

        return notificationRepository.findAllByUserId(userId, NotificationStatus.of(status), pageable)
            .map { notification -> NotificationHistoryResponse.of(notification) }
    }

    @Transactional
    override fun cancelNotification(userId: Long, id: Long) {
        if (!userRepository.existsById(userId)) throw NotFoundException(USER_NOT_FOUND)

        val notification = notificationRepository.findWithPessimisticLockById(id)
            .orElseThrow { throw NotFoundException(NOTIFICATION_NOT_FOUND) }

        notification.checkCancelableStatus()

        notification.cancelNotification()
    }

    @Transactional
    override fun getReservedNotifications(): List<Notification> {
        val reservedNotifications =
            notificationRepository.findAllWithPessimisticLockByReserved(LocalDateTime.now())

        for (notification in reservedNotifications) {
            notification.changeStatus(NotificationStatus.PENDING)
        }

        return reservedNotifications
    }

    @Transactional
    override fun sendNotificationSuccess(
        notificationId: Long
    ) {
        val notification = notificationRepository.findById(notificationId)
            .orElseThrow { throw NotFoundException(NOTIFICATION_NOT_FOUND) }

        notification.increaseAttemptCount()
        notification.changeStatus(NotificationStatus.SENT)

        createNotificationAttempt(notification, true)
    }

    @Transactional
    override fun sendNotificationFailure(
        notificationId: Long
    ) {
        val notification = notificationRepository.findById(notificationId)
            .orElseThrow { throw NotFoundException(NOTIFICATION_NOT_FOUND) }

        notification.handleNotificationFailure()

        createNotificationAttempt(notification, false)
    }

    private fun createNotificationAttempt(
        notification: Notification,
        success: Boolean
    ) {
        val attemptNo = notification.attemptCount

        val notificationAttemptResult =
            if (success) NotificationAttemptResult.SUCCESS else NotificationAttemptResult.FAILURE

        val notificationAttempt =
            NotificationAttempt.create(notification.id!!, attemptNo, notificationAttemptResult)

        notificationAttemptRepository.save(notificationAttempt)
    }
}