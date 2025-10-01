package com.dev.notificationapp.common.exeption.dto

import org.springframework.http.HttpStatus
import java.time.LocalDateTime

data class ErrorResponse(
    val error: String,
    val statusCode: Int,
    val timestamp: String,
    val message: String
) {
    companion object {
        fun of(httpStatus: HttpStatus, message: String?): ErrorResponse {
            return ErrorResponse(
                httpStatus.reasonPhrase,
                httpStatus.value(),
                LocalDateTime.now().toString(),
                message ?: "오류가 발생했습니다."
            )
        }
    }
}
