package com.dev.notificationapp.domain.notification

import com.dev.notificationapp.common.entity.BaseEntity
import com.dev.notificationapp.domain.notification.enums.NotificationAttemptResult
import jakarta.persistence.*

@Entity
@Table(name = "notification_attempt")
class NotificationAttempt(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    val notification: Notification,

    val attemptNo: Int,

    @Enumerated(EnumType.STRING)
    val result: NotificationAttemptResult
) : BaseEntity() {

    companion object {
        fun create(
            notification: Notification,
            attemptNo: Int,
            attemptResult: NotificationAttemptResult
        ) : NotificationAttempt {
            return NotificationAttempt(
                notification = notification,
                attemptNo = attemptNo,
                result = attemptResult
            )
        }
    }
}