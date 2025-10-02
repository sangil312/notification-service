package com.dev.notificationapp.domain.notification.enums

enum class OrderCondition(
    val value: String
) {
    CREATED_AT("createdAt"),
    RESERVED_AT("reservedAt");

    companion object {
        fun existsByCondition(value: String): Boolean {
            return OrderCondition.entries
                .any { orderCondition ->  orderCondition.value == value }
        }
    }
}