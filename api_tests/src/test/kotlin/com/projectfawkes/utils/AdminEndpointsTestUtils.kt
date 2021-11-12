package com.projectfawkes.utils

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.projectfawkes.BASE_URL
import com.projectfawkes.responseObjects.CompleteUser
import com.projectfawkes.restTemplate
import org.springframework.http.*
import org.springframework.web.util.UriComponentsBuilder

const val USERS_ENDPOINT = "/users"
const val PROMOTE_DEMOTE_ENDPOINT = "/promote-demote"
const val ENABLE_DISABLE_ENDPOINT = "/enable-disable"

fun getUsers(username: String, uid: String?): List<CompleteUser> {
    val headers = HttpHeaders()
    headers.set("testUsername", username)
    val request: HttpEntity<String> = HttpEntity(headers)
    val builder: UriComponentsBuilder = UriComponentsBuilder
        .fromHttpUrl("$BASE_URL$USERS_ENDPOINT")
    if (!uid.isNullOrBlank()) builder.queryParam("uid", uid)
    val response: ResponseEntity<String> =
        restTemplate.exchange(builder.toUriString(), HttpMethod.GET, request, String::class.java)

    return jacksonObjectMapper().readValue(response.body ?: "")
}

fun promoteDemoteAccount(username: String, accountUID: String, promoted: Boolean): ResponseEntity<String> {
    val headers = HttpHeaders()
    headers.contentType = MediaType.APPLICATION_JSON
    headers.set("testUsername", username)
    val body = mutableMapOf("uid" to accountUID, "promoted" to promoted)

    val request = HttpEntity(body, headers)
    return restTemplate.exchange(
        "$BASE_URL$USERS_ENDPOINT$PROMOTE_DEMOTE_ENDPOINT",
        HttpMethod.POST,
        request,
        String::class.java
    )
}

fun enableDisableAccount(username: String, accountUID: String, enabled: Boolean): ResponseEntity<String> {
    val headers = HttpHeaders()
    headers.contentType = MediaType.APPLICATION_JSON
    headers.set("testUsername", username)
    val body = mutableMapOf("uid" to accountUID, "enabled" to enabled)

    val request = HttpEntity(body, headers)
    return restTemplate.exchange(
        "$BASE_URL$USERS_ENDPOINT$ENABLE_DISABLE_ENDPOINT",
        HttpMethod.POST,
        request,
        String::class.java
    )
}