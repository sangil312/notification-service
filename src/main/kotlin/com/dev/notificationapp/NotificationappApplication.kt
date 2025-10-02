package com.dev.notificationapp

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@SpringBootApplication
class NotificationappApplication

fun main(args: Array<String>) {
	runApplication<NotificationappApplication>(*args)
}
