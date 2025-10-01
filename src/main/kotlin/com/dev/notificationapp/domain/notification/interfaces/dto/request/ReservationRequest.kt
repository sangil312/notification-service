package com.dev.notificationapp.domain.notification.interfaces.dto.request

import com.dev.notificationapp.domain.notification.application.dto.request.ReservationServiceRequest
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime

data class ReservationRequest(
    @field:NotEmpty(message = "알림 제목은 필수입니다.")
    val title: String,

    @field:NotEmpty(message = "알림 내용은 필수입니다.")
    val contents: String,

    @field:NotNull(message = "알림 발송 예약 시간은 필수입니다.")
    val reserveTime: LocalDateTime
) {
    fun toServiceRequest(): ReservationServiceRequest {
        return ReservationServiceRequest(title, contents, reserveTime)
    }
}
