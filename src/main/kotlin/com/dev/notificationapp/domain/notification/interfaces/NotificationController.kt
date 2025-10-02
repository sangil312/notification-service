package com.dev.notificationapp.domain.notification.interfaces

import com.dev.notificationapp.domain.notification.application.NotificationService
import com.dev.notificationapp.domain.notification.application.dto.response.NotificationHistoryResponse
import com.dev.notificationapp.domain.notification.application.dto.response.ReservationResponse
import com.dev.notificationapp.domain.notification.interfaces.dto.request.ReservationRequest
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

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
        @RequestParam("status") status: String?,
        pageable: Pageable
        ) : ResponseEntity<Page<NotificationHistoryResponse>> {
        // 토큰에서 userId를 추출했다고 가정
        return ResponseEntity.ok(notificationService.getNotifications(1L, status, pageable))
    }

    @PatchMapping("/api/notifications/{id}/status")
    fun cancelNotification(
        @PathVariable("id") id: Long
    ) : ResponseEntity<Void> {
        // 토큰에서 userId를 추출했다고 가정
        notificationService.cancelNotification(1L, id)
        return ResponseEntity.ok().build()
    }
}