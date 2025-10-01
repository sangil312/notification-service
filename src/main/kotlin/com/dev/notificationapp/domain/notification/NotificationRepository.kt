package com.dev.notificationapp.domain.notification

import org.springframework.data.jpa.repository.JpaRepository

interface NotificationRepository : JpaRepository<Notification, Long>, NotificationCustomRepository {
    fun existsByIdempotencyKey(idempotencyKey: String): Boolean
}