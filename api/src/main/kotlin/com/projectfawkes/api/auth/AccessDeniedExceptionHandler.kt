package com.projectfawkes.api.auth

import org.springframework.http.HttpStatus
import org.springframework.security.web.access.AccessDeniedHandler
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


class AccessDeniedExceptionHandler : AccessDeniedHandler {
    override fun handle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        ex: org.springframework.security.access.AccessDeniedException
    ) {
        response.status = HttpStatus.FORBIDDEN.value()
    }
}