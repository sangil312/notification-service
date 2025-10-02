package com.dev.notificationapp.domain.notification

import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import java.util.*

interface NotificationRepository : JpaRepository<Notification, Long>, NotificationCustomRepository {
    fun existsByIdempotencyKey(idempotencyKey: String): Boolean

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    fun findWithPessimisticLockById(id: Long): Optional<Notification>
}