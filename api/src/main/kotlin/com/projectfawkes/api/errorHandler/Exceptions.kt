package com.projectfawkes.api.errorHandler

import org.springframework.validation.BindingResult

class DataNotFoundException : Exception {
    constructor(errorMessage: String) : super(errorMessage)
    constructor(errorMessage: String, err: Throwable) : super(errorMessage, err)
}

class DataConflictException : Exception {
    constructor(errorMessage: String) : super(errorMessage)
    constructor(errorMessage: String, err: Throwable) : super(errorMessage, err)
}

class UnauthorizedException(errorMessage: String?) : Exception(errorMessage)

data class ValidationException(val validationErrors: BindingResult) :
    Exception("Error validating input $validationErrors")