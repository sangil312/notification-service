package com.dev.notificationapp.common.exeption

import com.dev.notificationapp.common.exeption.enums.ErrorCode

class DuplicateException(errorCode: ErrorCode) :CustomException(errorCode) {
}