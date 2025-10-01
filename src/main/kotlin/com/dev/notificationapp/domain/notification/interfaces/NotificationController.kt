package com.dev.notificationapp.domain.notification.interfaces

import com.dev.notificationapp.domain.notification.application.NotificationService
import com.dev.notificationapp.domain.notification.application.dto.response.NotificationHistoryResponse
import com.dev.notificationapp.domain.notification.application.dto.response.ReservationResponse
import com.dev.notificationapp.domain.notification.interfaces.dto.request.ReservationRequest
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController

@RestController
class NotificationController(
    private val notificationService: NotificationService
) {
    @PostMapping("/api/notifications")
    fun reserveNotification(
        @RequestHeader("Idempotency-Key") idempotencyKey: String,
        @Valid @RequestBody request: ReservationRequest
    ) : ResponseEntity<ReservationResponse> {
        // 토큰에서 userId를 추출했다고 가정
        return ResponseEntity.ok(
            notificationService.reserveNotification(1L, idempotencyKey, request.toServiceRequest()))
    }

    @GetMapping("/api/notifications")
    fun getNotifications(
        pageable: Pageable
        ) : ResponseEntity<Page<NotificationHistoryResponse>> {
        // 토큰에서 userId를 추출했다고 가정
        return ResponseEntity.ok(notificationService.getNotifications(1L, pageable))
    }
}