package com.dev.notificationapp.domain.notification.interfaces

import com.dev.notificationapp.domain.notification.application.NotificationService
import com.dev.notificationapp.domain.notification.application.dto.response.NotificationHistoryResponse
import com.dev.notificationapp.domain.notification.application.dto.response.ReservationResponse
import com.dev.notificationapp.domain.notification.interfaces.dto.request.ReservationRequest
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDateTime


@WebMvcTest(NotificationController::class)
class NotificationControllerTest(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val objectMapper: ObjectMapper
) {
    @MockkBean lateinit var notificationService: NotificationService

    @DisplayName("알림 발송 예약을 접수한다.")
    @Test
    fun reserveNotification() {
        val now = LocalDateTime.now()
        val request = ReservationRequest("title", "contents", now)
        val response = ReservationResponse(
            1L, "RESERVED", true, now.toString())

        every { notificationService.reserveNotification(any(), any(), any()) } returns response

        mockMvc.perform(
            post("/api/notifications")
                .header("Idempotency-Key", "idempotencyKey")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.notificationId").value(1L))
            .andExpect(jsonPath("$.status").value("RESERVED"))
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.reservedTime").value(now.toString()))
    }

    @DisplayName("알림 발송 예약을 접수 시 요청 파라미터는 필수이다.")
    @Test
    fun reserveNotificationWithRequiredParameter() {
        val now = LocalDateTime.now()
        val request = ReservationRequest("", "contents", now)
        val response = ReservationResponse(
            1L, "RESERVED", true, now.toString())

        every { notificationService.reserveNotification(any(), any(), any()) } returns response

        mockMvc.perform(
            post("/api/notifications")
                .header("Idempotency-Key", "idempotencyKey")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.error").value("Bad Request"))
            .andExpect(jsonPath("$.statusCode").value(400))
            .andExpect(jsonPath("$.timestamp").isNotEmpty)
            .andExpect(jsonPath("$.message").value("알림 제목은 필수입니다."))
    }

    @DisplayName("알림 발송 접수 목록을 조회한다.")
    @Test
    fun getNotifications() {
        val now = LocalDateTime.now()

        val content = NotificationHistoryResponse(
            1L,
            "RESERVED",
            1,
            "title",
            now.toString(),
            now.toString(),
            now.plusSeconds(1).toString(),
        )
        val sort = Sort.by(Sort.Direction.DESC, "createdAt")

        val response = PageImpl(listOf(content), PageRequest.of(0, 10, sort), 1)


        every { notificationService.getNotifications(any(), any(), any()) } returns response

        mockMvc.perform(
            get("/api/notifications")
                .queryParam("status", "reserved")
                .queryParam("size", "10")
                .queryParam("page", "0")
                .queryParam("sort", "createdAt,desc"))
            .andDo(print())
            .andExpect(jsonPath("$.content[0].notificationId").value(1L))
            .andExpect(jsonPath("$.content[0].status").value("RESERVED"))
            .andExpect(jsonPath("$.content[0].attemptCount").value(1))
            .andExpect(jsonPath("$.content[0].title").value("title"))
            .andExpect(jsonPath("$.content[0].reservedTime").value(now.toString()))
            .andExpect(jsonPath("$.content[0].retryTime").value(now.toString()))
            .andExpect(jsonPath("$.content[0].acceptTime").value(now.plusSeconds(1).toString()))
    }

    @DisplayName("알림 발송 예약을 취소한다.")
    @Test
    fun cancelNotification() {
        every { notificationService.cancelNotification(any(), any()) } returns Unit

        mockMvc.perform(
            patch("/api/notifications/{id}/status", 1L))
            .andExpect(status().isOk)
    }
}