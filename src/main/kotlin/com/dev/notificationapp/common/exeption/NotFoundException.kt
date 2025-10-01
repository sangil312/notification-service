package com.dev.notificationapp.common.exeption

import com.dev.notificationapp.common.exeption.enums.ErrorCode

class NotFoundException(errorCode: ErrorCode) : CustomException(errorCode) {
}