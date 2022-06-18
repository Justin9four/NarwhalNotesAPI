package com.projectfawkes.api.errorHandler

import com.projectfawkes.api.auth.AuthType
import com.projectfawkes.api.auth.UseAuth
import com.projectfawkes.api.controller.API_ENDPOINT
import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletResponse

const val ERROR_ENDPOINT = "/error"

@RestController
@UseAuth(AuthType.PUBLIC)
class ErrorHandler : ErrorController {

    @GetMapping(ERROR_ENDPOINT)
    fun handleError(httpServletResponse: HttpServletResponse): String {
        httpServletResponse.status = 404
        val errorMessage = "This is not a valid endpoint. For help refer to "
        val redirectLink = "<a href=\"$API_ENDPOINT\">API Documentation</a>"
        return "$errorMessage $redirectLink"
    }

    override fun getErrorPath(): String {
        return "/error"
    }
}