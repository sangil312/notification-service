package com.dev.notificationapp

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@SpringBootTest
abstract class IntegrationTestSupport {
}