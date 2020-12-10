package com.projectfawkes.utils

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.projectfawkes.AuthManager
import com.projectfawkes.BABY_YODA
import com.projectfawkes.BASE_URL
import com.projectfawkes.responseObjects.Account
import com.projectfawkes.responseObjects.AuthenticationObject
import com.projectfawkes.responseObjects.UpdateUser
import com.projectfawkes.responseObjects.User
import com.projectfawkes.restTemplate
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.http.*
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.util.UriComponentsBuilder

private val logger: Logger = LogManager.getLogger()
const val REGISTER_ENDPOINT = "/register"
const val USER_ENDPOINT = "/user"
const val AUTHENTICATE_ENDPOINT = "/authenticate"

fun createUser(authManager: AuthManager, firstName: String, lastName: String, email: String, dob: String): Account {
    val headers = HttpHeaders()
    headers.contentType = MediaType.APPLICATION_FORM_URLENCODED
    val map: MultiValueMap<String, String> = LinkedMultiValueMap()
    map.add("serviceAccountId", "F68SDYGEHV79RG9W834CTY89WY7T8FCW84NHT7830WTHCF7HFT4F78ERHC78RGH748R7804TCH79MPSUGSY7459H9A")
    map.add("key", BABY_YODA)

    map.add("firstName", firstName)
    map.add("lastName", lastName)
    map.add("email", email)
    map.add("username", authManager.uid)
    map.add("password", authManager.password)
    map.add("dob", dob)
    val request = HttpEntity(map, headers)

    val response: ResponseEntity<String> = restTemplate.exchange("$BASE_URL$REGISTER_ENDPOINT", HttpMethod.PUT, request, String::class.java)
    authManager.authToken = response.headers.getFirst("Set-Cookie")!!

    return jacksonObjectMapper().readValue(response.body ?: "")
}

fun authenticate(authManager: AuthManager): Account {
    val builder: UriComponentsBuilder = UriComponentsBuilder
            .fromHttpUrl("$BASE_URL$AUTHENTICATE_ENDPOINT")
    val headers = HttpHeaders()
    val map: MultiValueMap<String, String> = LinkedMultiValueMap()
    if (!authManager.authToken.isNullOrBlank()) {
        authManager.addAuthTokenToRequest(headers)
        logger.info("Authenticated with auth token")
    } else {
        map.add("username", authManager.uid)
        map.add("password", authManager.password)
        logger.info("Authenticated with basic auth")
    }
    headers.contentType = MediaType.APPLICATION_FORM_URLENCODED
    val request = HttpEntity(map, headers)
    val response: ResponseEntity<String> = restTemplate.exchange(builder.toUriString(), HttpMethod.POST, request, String::class.java)
    if (authManager.authToken.isNullOrBlank()) {
        authManager.authToken = response.headers.getFirst("Set-Cookie")!!
    }
    return jacksonObjectMapper().readValue(response.body ?: "")
}

fun getUser(authManager: AuthManager): User {
    val builder: UriComponentsBuilder = UriComponentsBuilder
            .fromHttpUrl("$BASE_URL$USER_ENDPOINT")
    val headers = HttpHeaders()
    headers.set("testUsername", authManager.uid)
    headers.contentType = MediaType.APPLICATION_FORM_URLENCODED
    val request = HttpEntity<String>(headers)
    val response: ResponseEntity<String> = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, request, String::class.java)
    return jacksonObjectMapper().readValue(response.body ?: "")
}

fun updateUser(authManager: AuthManager, updateUserObject: UpdateUser): ResponseEntity<String> {
    val headers = HttpHeaders()
    headers.contentType = MediaType.APPLICATION_FORM_URLENCODED
    headers.set("testUsername", authManager.uid)
    val map: MultiValueMap<String, String> = LinkedMultiValueMap()

    if (!updateUserObject.firstName.isNullOrBlank()) map.add("firstName", updateUserObject.firstName)
    if (!updateUserObject.lastName.isNullOrBlank()) map.add("lastName", updateUserObject.lastName)
    if (!updateUserObject.username.isNullOrBlank()) map.add("username", updateUserObject.username)
    if (!updateUserObject.dob.isNullOrBlank()) map.add("dob", updateUserObject.dob)
    if (!updateUserObject.password.isNullOrBlank()) map.add("password", updateUserObject.password)
    if (!updateUserObject.photoUrl.isNullOrBlank()) map.add("photoUrl", updateUserObject.photoUrl)

    val request = HttpEntity(map, headers)
    return restTemplate.exchange("$BASE_URL$USER_ENDPOINT", HttpMethod.POST, request, String::class.java)
}

fun deleteUser(authManager: AuthManager): ResponseEntity<String> {
    val headers = HttpHeaders()
    headers.set("testUsername", authManager.uid)
    headers.contentType = MediaType.APPLICATION_FORM_URLENCODED
    val map: MultiValueMap<String, String> = LinkedMultiValueMap()
    val request = HttpEntity(map, headers)

    return restTemplate.exchange("$BASE_URL$USER_ENDPOINT", HttpMethod.DELETE, request, String::class.java)
}
