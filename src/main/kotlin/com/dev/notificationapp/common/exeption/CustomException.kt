package com.dev.notificationapp.common.exeption

import com.dev.notificationapp.common.exeption.enums.ErrorCode

open class CustomException(
    val errorCode: ErrorCode
) : RuntimeException() {
}