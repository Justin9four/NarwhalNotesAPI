package com.projectfawkes

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.web.client.RestTemplate
import java.nio.charset.Charset
import java.util.*
import kotlin.system.exitProcess

const val BABY_YODA = "YK=,jqF^=,b\$Xc?hwbe2/Wk~~'~,?Q"
var BASE_URL = "http://localhost:8080/api"
val restTemplate = RestTemplate()
private val logger: Logger = LogManager.getLogger()

fun testConnection() {
    val headers = HttpEntity<String>(HttpHeaders())
    try {
        restTemplate.exchange(BASE_URL, HttpMethod.GET, headers, String::class.java)
    } catch (e: Exception) {
        logger.info("HTTP request fail. Restart local instance")
        exitProcess(1)
    }
}

class AuthManager (var uid: String, var password: String) {
    var authToken: String? = null

    fun addAuthTokenToRequest(headers: HttpHeaders) {
        headers.add("Cookie", authToken!!)
    }

    fun addBasicAuthToRequest(headers: HttpHeaders) {
        val auth = "$uid:$password"
        val encodedAuth: ByteArray = Base64.getEncoder().encode(
                auth.toByteArray(Charset.forName("US-ASCII")))
        val authHeader = "Basic " + String(encodedAuth)
        headers.add("Authorization", authHeader)
    }
}