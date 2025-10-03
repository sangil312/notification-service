package com.dev.notificationapp.domain.notification

import com.dev.notificationapp.domain.notification.enums.NotificationStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.time.LocalDateTime

interface NotificationCustomRepository {
    fun findAllByUserId(
        userId: Long,
        status: NotificationStatus?,
        pageable: Pageable
    ): Page<Notification>

    fun findAllWithPessimisticLockByReserved(
        schedulerTime: LocalDateTime
    ) : List<Notification>
}