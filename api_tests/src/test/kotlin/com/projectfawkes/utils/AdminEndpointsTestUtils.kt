package com.projectfawkes.utils

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.projectfawkes.BASE_URL
import com.projectfawkes.responseObjects.User
import com.projectfawkes.restTemplate
import org.springframework.http.*
import org.springframework.web.util.UriComponentsBuilder

const val USERS_ENDPOINT = "/users"
const val PROMOTE_ACCOUNT_ENDPOINT = "/promoteAccount"

fun getUsers(username: String): List<User> {
    val builder: UriComponentsBuilder = UriComponentsBuilder
        .fromHttpUrl("$BASE_URL$USERS_ENDPOINT")
    val headers = HttpHeaders()
    headers.set("testUsername", username)
    headers.contentType = MediaType.APPLICATION_JSON
    val request = HttpEntity<String>(headers)
    val response: ResponseEntity<String> =
        restTemplate.exchange(builder.toUriString(), HttpMethod.GET, request, String::class.java)

    return jacksonObjectMapper().readValue(response.body ?: "")
}

fun promoteAccount(username: String, accountUID: String): ResponseEntity<String> {
    val headers = HttpHeaders()
    headers.contentType = MediaType.APPLICATION_JSON
    headers.set("testUsername", username)
    val body = mutableMapOf("uid" to accountUID)

    val request = HttpEntity(body, headers)
    return restTemplate.exchange("$BASE_URL$PROMOTE_ACCOUNT_ENDPOINT", HttpMethod.POST, request, String::class.java)
}