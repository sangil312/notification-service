package com.dev.notificationapp.domain.notification.enums

import lombok.Getter

@Getter
enum class NotificationStatus {
    RESERVED,
    SENT,
    CANCELED,
    FAILED,
}
