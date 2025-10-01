package com.dev.notificationapp.common.exeption

import com.dev.notificationapp.common.exeption.enums.ErrorCode

class InvalidTimeException(errorCode: ErrorCode) : CustomException(errorCode) {
}