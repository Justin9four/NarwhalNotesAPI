package com.projectfawkes.api.endpoints

import com.projectfawkes.api.API_ENDPOINT
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(API_ENDPOINT)
class ApiEndpoints {

    @GetMapping("")
    fun getApiHomepage() = ResponseEntity("Welcome to the Project Fawkes API", HttpStatus.OK)
}
