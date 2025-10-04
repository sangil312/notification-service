package com.dev.notificationapp.domain.notification

import com.dev.notificationapp.common.entity.BaseEntity
import com.dev.notificationapp.common.exeption.NotificationCancelException
import com.dev.notificationapp.common.exeption.enums.ErrorCode.*
import com.dev.notificationapp.domain.notification.application.dto.request.ReservationServiceRequest
import com.dev.notificationapp.domain.notification.enums.NotificationStatus
import com.dev.notificationapp.domain.user.User
import jakarta.persistence.*
import org.hibernate.annotations.DynamicUpdate
import java.time.LocalDateTime

@Entity
@DynamicUpdate
@Table(name = "notification")
class Notification(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    val user: User,

    val idempotencyKey: String,

    @Enumerated(EnumType.STRING)
    var status: NotificationStatus,

    var attemptCount: Int,

    var maxAttemptCount: Int,

    val phoneNumber: String,

    val title: String,

    val contents: String,

    val reservedAt: LocalDateTime,

    var retryAt: LocalDateTime,

) : BaseEntity() {

    companion object {
        fun register(
            user: User,
            idempotencyKey: String,
            request: ReservationServiceRequest
        ) : Notification {
            return Notification(
                user = user,
                idempotencyKey = idempotencyKey,
                status = NotificationStatus.RESERVED,
                attemptCount = 0,
                maxAttemptCount = 3,
                phoneNumber = user.phoneNumber,
                title = request.title,
                contents = request.contents,
                reservedAt = request.reserveTime,
                retryAt = request.reserveTime
            )
        }
    }

    fun changeStatus(status: NotificationStatus) {
        this.status = status
    }

    fun cancelNotification() {
        changeStatus(NotificationStatus.CANCELED)
    }

    fun checkCancelableStatus() {
        when (status) {
            NotificationStatus.PENDING -> throw NotificationCancelException(NOTIFICATION_PENDING)
            NotificationStatus.SENT -> throw NotificationCancelException(NOTIFICATION_SENT)
            NotificationStatus.CANCELED -> throw NotificationCancelException(NOTIFICATION_CANCELED)
            NotificationStatus.FAILED -> throw NotificationCancelException(NOTIFICATION_FAILED)
            else -> return
        }
    }

    fun increaseAttemptCount() {
        attemptCount ++
    }

    fun handleNotificationFailure() {
        increaseAttemptCount()

        if (attemptCount >= maxAttemptCount) {
            changeStatus(NotificationStatus.FAILED)
        } else {
            retryAt = retryAt.plusSeconds(10)
            changeStatus(NotificationStatus.RESERVED)
        }
    }
}