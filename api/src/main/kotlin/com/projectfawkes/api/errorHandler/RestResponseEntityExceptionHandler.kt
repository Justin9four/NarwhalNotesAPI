package com.projectfawkes.api.errorHandler

import com.projectfawkes.api.controller.dto.ValidationErrorDto
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.validation.ObjectError
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.util.stream.Collectors


@ControllerAdvice
class RestResponseEntityExceptionHandler : ResponseEntityExceptionHandler() {
    @ExceptionHandler(value = [DataConflictException::class])
    protected fun handleConflict(
            ex: Exception?, request: WebRequest?): ResponseEntity<Any> {
        val bodyOfResponse = ex?.message ?: "Data Conflict"
        return handleExceptionInternal(ex!!, bodyOfResponse,
                HttpHeaders(), HttpStatus.CONFLICT, request!!)
    }

    @ExceptionHandler(value = [KotlinNullPointerException::class])
    protected fun handleNullPointer(
            ex: Exception?, request: WebRequest?): ResponseEntity<Any> {
        val bodyOfResponse = ex?.message ?: "Server Error"
        return handleExceptionInternal(
            ex!!, bodyOfResponse,
            HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request!!
        )
    }

    @ExceptionHandler(value = [ValidationException::class])
    protected fun handleValidationException(
        ex: ValidationException, request: WebRequest?
    ): ResponseEntity<Any> {
        val bodyOfResponse = mapErrorsToValidationErrorDto(ex.validationErrors)
        return handleExceptionInternal(
            ex, bodyOfResponse,
            HttpHeaders(), HttpStatus.BAD_REQUEST, request!!
        )
    }

    @ExceptionHandler(value = [DataNotFoundException::class])
    protected fun handleNotFound(
            ex: Exception?, request: WebRequest?): ResponseEntity<Any> {
        val bodyOfResponse = ex?.message ?: "Data Not Found"
        return handleExceptionInternal(ex!!, bodyOfResponse,
            HttpHeaders(), HttpStatus.NOT_FOUND, request!!
        )
    }

    @ExceptionHandler(value = [UnauthorizedException::class])
    protected fun handleUnauthorized(
        ex: Exception?, request: WebRequest?
    ): ResponseEntity<Any> {
        val bodyOfResponse = ex?.message ?: "Unauthorized"
        return handleExceptionInternal(
            ex!!, bodyOfResponse,
            HttpHeaders(), HttpStatus.UNAUTHORIZED, request!!
        )
    }

    private fun mapErrorsToValidationErrorDto(errors: BindingResult): ValidationErrorDto {
        val errorMessages = errors
            .allErrors
            .stream()
            .map { error: ObjectError -> error.defaultMessage ?: "Validation Error" }
            .collect(Collectors.toList())
        return ValidationErrorDto(errorMessages)
    }
}