package com.dev.notificationapp.domain.notification

import org.springframework.data.jpa.repository.JpaRepository

interface NotificationAttemptRepository : JpaRepository<NotificationAttempt, Long> {
}