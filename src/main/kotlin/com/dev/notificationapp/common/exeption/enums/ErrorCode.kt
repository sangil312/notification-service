package com.dev.notificationapp.common.exeption.enums

import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.*

enum class ErrorCode(
    val httpStatus: HttpStatus,
    val message: String
) {
    /* 400 */
    RESERVATION_TIME_AFTER_NOW(BAD_REQUEST, "발송 예약은 현재 시간보다 늦은 시간이어야 합니다."),
    RESERVATION_TIME_LIMIT(BAD_REQUEST, "발송 예약은 접수 시간 기준 최대 +2시간 까지 가능합니다."),
    NOTIFICATION_SENT(BAD_REQUEST, "이미 발송된 알림입니다."),
    NOTIFICATION_CANCELED(BAD_REQUEST, "이미 취소된 알림입니다."),

    /* 404 */
    USER_NOT_FOUND(NOT_FOUND, "사용자를 찾을 수 없습니다."),
    NOTIFICATION_NOT_FOUND(NOT_FOUND, "발송 예약을 찾을 수 없습니다."),

    /* 409 */
    DUPLICATE_REQUEST(CONFLICT, "이미 처리된 요청입니다."),
}