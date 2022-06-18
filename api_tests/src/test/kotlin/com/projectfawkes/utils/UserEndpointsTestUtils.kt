package com.projectfawkes.utils

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.projectfawkes.BASE_URL
import com.projectfawkes.addBasicAuthToRequest
import com.projectfawkes.api.controller.API_ENDPOINT
import com.projectfawkes.api.controller.AUTHENTICATE_ENDPOINT
import com.projectfawkes.api.controller.REGISTER_ENDPOINT
import com.projectfawkes.api.controller.USERS_ENDPOINT
import com.projectfawkes.responseObjects.Account
import com.projectfawkes.responseObjects.UpdateUser
import com.projectfawkes.responseObjects.User
import com.projectfawkes.restTemplate
import org.springframework.http.*
import org.springframework.web.util.UriComponentsBuilder

fun createUser(
    username: String, password: String,
    firstName: String, lastName: String, email: String, dob: String
): Account {
    val headers = HttpHeaders()
    headers.contentType = MediaType.APPLICATION_JSON
    addBasicAuthToRequest(headers)
    val body = mapOf(
        "firstName" to firstName, "lastName" to lastName, "email" to email,
        "username" to username, "password" to password, "dob" to dob
    )
    val request = HttpEntity(body, headers)

    val response: ResponseEntity<String> =
        restTemplate.exchange("$BASE_URL$API_ENDPOINT$REGISTER_ENDPOINT", HttpMethod.PUT, request, String::class.java)

    return jacksonObjectMapper().readValue(response.body ?: "")
}

fun authenticate(username: String, password: String): Account {
    val builder: UriComponentsBuilder = UriComponentsBuilder
        .fromHttpUrl("$BASE_URL$API_ENDPOINT$AUTHENTICATE_ENDPOINT")
    val headers = HttpHeaders()
    addBasicAuthToRequest(headers)
    val body = mapOf("username" to username, "password" to password)
    headers.contentType = MediaType.APPLICATION_JSON
    val request = HttpEntity(body, headers)
    val response: ResponseEntity<String> =
        restTemplate.exchange(builder.toUriString(), HttpMethod.POST, request, String::class.java)
    return jacksonObjectMapper().readValue(response.body ?: "")
}

fun getUser(username: String): User {
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

fun updateUser(username: String, updateUserObject: UpdateUser): ResponseEntity<String> {
    val headers = HttpHeaders()
    headers.contentType = MediaType.APPLICATION_JSON
    headers.set("testUsername", username)
    val body = mutableMapOf<String, String>()

    if (!updateUserObject.firstName.isNullOrBlank()) body["firstName"] = updateUserObject.firstName!!
    if (!updateUserObject.lastName.isNullOrBlank()) body["lastName"] = updateUserObject.lastName!!
    if (!updateUserObject.username.isNullOrBlank()) body["username"] = updateUserObject.username!!
    if (!updateUserObject.dob.isNullOrBlank()) body["dob"] = updateUserObject.dob!!
    if (!updateUserObject.password.isNullOrBlank()) body["password"] = updateUserObject.password!!
    if (!updateUserObject.photoUrl.isNullOrBlank()) body["photoUrl"] = updateUserObject.photoUrl!!

    val request = HttpEntity(body, headers)
    return restTemplate.exchange("$BASE_URL$USERS_ENDPOINT", HttpMethod.POST, request, String::class.java)
}

fun deleteUser(username: String): ResponseEntity<String> {
    val headers = HttpHeaders()
    headers.set("testUsername", username)
    headers.contentType = MediaType.APPLICATION_JSON
    val request = HttpEntity<String>(headers)

    return restTemplate.exchange("$BASE_URL$USERS_ENDPOINT", HttpMethod.DELETE, request, String::class.java)
}
