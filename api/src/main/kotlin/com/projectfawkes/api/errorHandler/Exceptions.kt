package com.projectfawkes.api.errorHandler

class DataNotFoundException : Exception {
    constructor(errorMessage: String) : super(errorMessage)
    constructor(errorMessage: String, err: Throwable) : super(errorMessage, err)
}

class DataConflictException : Exception {
    constructor(errorMessage: String) : super(errorMessage)
    constructor(errorMessage: String, err: Throwable) : super(errorMessage, err)
}

class UnauthorizedException(errorMessage: String?) : Exception(errorMessage)

class ValidationException(errorMessage: String, val validationError: MutableList<ValidationError>) : Exception(errorMessage)