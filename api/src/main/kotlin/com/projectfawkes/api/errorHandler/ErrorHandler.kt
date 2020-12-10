package com.projectfawkes.api.errorHandler

import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

const val ERROR_ENDPOINT = "/error"

@RestController
@RequestMapping("/")
class ErrorHandler : ErrorController {

    @GetMapping(ERROR_ENDPOINT)
    fun handleError(): String {
        //do something like logging
        return "TODO: Create custom error page"
    }

    override fun getErrorPath(): String {
        return "/error"
    }
}