package com.dev.notificationapp.domain.notification

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface NotificationCustomRepository {
    fun findAllByUserId(userId: Long, pageable: Pageable): Page<Notification>
}