package com.projectfawkes.api.controller

import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping(API_ENDPOINT)
class ApiController {

    @GetMapping(produces = [MediaType.TEXT_HTML_VALUE])
    fun getApiHomepage(httpServletResponse: HttpServletResponse): ResponseEntity<Any> {
        val gitHubLink = "<a href=\"https://github.com/Justin9four/NarwhalNotesAPI\">GitHub Repository</a>"
        val body =
            "Welcome to the Project Fawkes API <br/> This project is currently under \uD83D\uDC77construction\uD83D\uDC77. Please refer to my $gitHubLink for information or if you would like to contribute.<br/> An API Reference site is also in the works to replace this page."
        return ResponseEntity(body, HttpStatus.OK)
    }

    @GetMapping("**", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun routeNotFound(httpServletResponse: HttpServletResponse) {
        httpServletResponse.sendRedirect("/error")
    }
}
