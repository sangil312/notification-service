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

    var retryCount: Int,

    var maxRetry: Int,

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
                status = NotificationStatus.PENDING,
                retryCount = 0,
                maxRetry = 2,
                phoneNumber = user.phoneNumber,
                title = request.title,
                contents = request.contents,
                reservedAt = request.reserveTime,
                retryAt = request.reserveTime
            )
        }
    }

    /* 비지니스 메서드 */
    fun cancelNotification() {
        status = NotificationStatus.CANCELED
    }

    fun checkCancelableStatus() {
        when (status) {
            NotificationStatus.SENT -> throw NotificationCancelException(NOTIFICATION_SENT)
            NotificationStatus.CANCELED -> throw NotificationCancelException(NOTIFICATION_CANCELED)
            else -> return
        }
    }
}