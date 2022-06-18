package com.projectfawkes

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.projectfawkes.api.controller.API_ENDPOINT
import com.projectfawkes.responseObjects.ServiceAccount
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.web.client.RestTemplate
import java.nio.charset.Charset
import java.util.*
import kotlin.system.exitProcess

var BASE_URL = "http://localhost:8080"
val restTemplate = RestTemplate()
private val logger: Logger = LogManager.getLogger()

fun testConnection() {
    val headers = HttpEntity<String>(HttpHeaders())
    try {
        restTemplate.exchange("$BASE_URL$API_ENDPOINT", HttpMethod.GET, headers, String::class.java)
    } catch (e: Exception) {
        logger.info("HTTP request fail. Restart local instance")
        exitProcess(1)
    }
}

fun addBasicAuthToRequest(headers: HttpHeaders) {
    val serviceAccountsJSON = System.getenv("TestServiceAccounts")
    val serviceAccounts: List<ServiceAccount> = jacksonObjectMapper().readValue(serviceAccountsJSON!!)
    val serviceAccountName = serviceAccounts[0].accountName
    val serviceAccountPassword = serviceAccounts[0].password
    val auth = "$serviceAccountName:$serviceAccountPassword"
    val encodedAuth: ByteArray = Base64.getEncoder().encode(
        auth.toByteArray(Charset.forName("US-ASCII")))
    val authHeader = "Basic " + String(encodedAuth)
    headers.add("Authorization", authHeader)
}