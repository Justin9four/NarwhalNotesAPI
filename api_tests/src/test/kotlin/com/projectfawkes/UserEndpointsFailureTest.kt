package com.projectfawkes

import API_ENDPOINT
import AUTHENTICATE_ENDPOINT
import REGISTER_ENDPOINT
import USERS_ENDPOINT
import com.projectfawkes.utils.createUser
import com.projectfawkes.utils.deleteUser
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.http.*
import org.springframework.web.client.HttpClientErrorException
import org.testng.annotations.AfterClass
import org.testng.annotations.BeforeClass
import org.testng.annotations.DataProvider
import org.testng.annotations.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class UserEndpointsFailureTest {
    private val logger: Logger = LogManager.getLogger()
    private var username = "testUser123"
    private var lastName = "lastName"
    private var firstName = "firstName"
    private var email = "email@example.com"
    private var password = "testBabyYodaIsAwesome^2194ThisIsAPassword"
    private var dob = "12 25 1996"

    data class ValidationError(val errorCode: Int, val details: ValidationErrorDetails)

    data class ValidationErrorDetails(val errorMessage: String, val fields: MutableList<String> = mutableListOf())

    @BeforeClass
    fun setUp() {
        testConnection()
        createUser(username, password, firstName, lastName, email, dob)
    }

    @AfterClass
    fun tearDown() {
        deleteUser(username)
        logger.info("Test user deleted: $username")
    }

    @Test
    fun createUserUsernameAndEmailConflict() {
        try {
            createUser(username, password, firstName, lastName, email, dob)
        } catch (e: HttpClientErrorException) {
            if(e.rawStatusCode == 409) {
                assertEquals("[username, email] not unique", e.responseBodyAsString)
                return
            }
        }
        fail()
    }

    @Test(dataProvider = "createUserMissingField")
    fun createUserMissingField(body: Map<String, String>) {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        addBasicAuthToRequest(headers)
        val request = HttpEntity(body, headers)

        try {
            restTemplate.exchange(
                "$BASE_URL$API_ENDPOINT$REGISTER_ENDPOINT",
                HttpMethod.POST,
                request,
                String::class.java
            )
            fail()
        } catch (e: HttpClientErrorException) {
            if (e.statusCode != HttpStatus.BAD_REQUEST) {
                fail()
            }
        }
    }

    @Test
    fun createUserUnauthorized() {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        val request = HttpEntity(mutableMapOf<String, String>(), headers)

        try {
            restTemplate.exchange(
                "$BASE_URL$API_ENDPOINT$REGISTER_ENDPOINT",
                HttpMethod.POST,
                request,
                String::class.java
            )
            fail()
        } catch (e: HttpClientErrorException) {
            if (e.statusCode != HttpStatus.UNAUTHORIZED) {
                fail()
            }
        }
    }

    @DataProvider(name = "createUserMissingField")
    fun createUserMissingField(): MutableIterator<Array<Any>> {
        return arrayListOf<Array<Any>>(
            arrayOf(
                mapOf("username" to "username", "password" to "password")
            ),
            arrayOf(
                mapOf(
                    "email" to "email", "password" to "password", "lastName" to "lastName", "firstName" to "firstName",
                    "username" to "username"
                )
            )
        ).iterator()
    }

    @Test(dataProvider = "unauthorizedTest")
    fun unauthorizedTest(url: String, httpMethod: HttpMethod) {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        val request = HttpEntity<String>(headers)
        try {
            restTemplate.exchange(url, httpMethod, request, String::class.java)
            fail()
        } catch (e: HttpClientErrorException) {
            if (e.statusCode != HttpStatus.UNAUTHORIZED) {
                fail()
            }
        }
    }

    @DataProvider(name = "unauthorizedTest")
    fun unauthorizedTest(): MutableIterator<Array<Any>> {
        return arrayListOf<Array<Any>>(
            arrayOf("$BASE_URL$API_ENDPOINT$AUTHENTICATE_ENDPOINT", HttpMethod.POST),
            arrayOf("$BASE_URL$USERS_ENDPOINT", HttpMethod.DELETE)
        ).iterator()
    }

    private fun addError(errorCode: Int, errorMessage: String, fields: List<String>): ValidationError {
        val validationError = ValidationError(errorCode, ValidationErrorDetails(errorMessage))
        validationError.details.fields.addAll(fields)
        return validationError
    }
}
