package com.dev.notificationapp.domain.notification.enums

enum class NotificationStatus {
    RESERVED,
    SENT,
    CANCELED,
    FAILED;

    companion object {
        fun of(value : String?) : NotificationStatus? {
            return NotificationStatus.entries.firstOrNull { status -> status.name.equals(value, ignoreCase = true) }
        }
    }
}
