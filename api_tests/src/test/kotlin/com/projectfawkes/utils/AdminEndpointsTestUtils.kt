package com.projectfawkes.utils

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.projectfawkes.BASE_URL
import com.projectfawkes.api.controller.ADMIN_ENABLE_DISABLE_ACCOUNT_ENDPOINT
import com.projectfawkes.api.controller.ADMIN_ENDPOINT
import com.projectfawkes.api.controller.ADMIN_PROMOTE_DEMOTE_ACCOUNT_ENDPOINT
import com.projectfawkes.responseObjects.CompleteUser
import com.projectfawkes.restTemplate
import org.springframework.http.*
import org.springframework.web.util.UriComponentsBuilder

fun getUsers(username: String, uid: String?): List<CompleteUser> {
    val headers = HttpHeaders()
    headers.set("testUsername", username)
    val request: HttpEntity<String> = HttpEntity(headers)
    val builder: UriComponentsBuilder = UriComponentsBuilder
        .fromHttpUrl("$BASE_URL$ADMIN_ENDPOINT")
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
        "$BASE_URL$ADMIN_ENDPOINT$ADMIN_PROMOTE_DEMOTE_ACCOUNT_ENDPOINT",
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
        "$BASE_URL$ADMIN_ENDPOINT$ADMIN_ENABLE_DISABLE_ACCOUNT_ENDPOINT",
        HttpMethod.POST,
        request,
        String::class.java
    )
}