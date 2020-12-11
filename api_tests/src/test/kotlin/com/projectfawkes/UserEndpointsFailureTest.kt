package com.projectfawkes

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.projectfawkes.utils.*
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.http.*
import org.springframework.util.LinkedMultiValueMap
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
    private val authManager = AuthManager(username, password)

    data class ValidationError(val errorCode: Int, val details: ValidationErrorDetails)

    data class ValidationErrorDetails(val errorMessage: String, val fields: MutableList<String> = mutableListOf())

    @BeforeClass
    fun setUp() {
        testConnection()
        createUser(authManager, firstName, lastName, email, dob)
    }

    @AfterClass
    fun tearDown() {
        deleteUser(authManager)
        logger.info("Test user deleted: ${authManager.uid}")
    }

    @Test
    fun createUserUsernameAndEmailConflict() {
        try {
            createUser(authManager, firstName, lastName, email, dob)
        } catch (e: HttpClientErrorException) {
            if(e.rawStatusCode == 409) {
                assertEquals("[username, email] not unique", e.responseBodyAsString)
                return
            }
        }
        fail()
    }

    @Test(dataProvider = "createUserMissingField")
    fun createUserMissingField(body: LinkedMultiValueMap<String, String>, validationError: ValidationError) {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED
        addBasicAuthToRequest(headers)
        val request = HttpEntity(body, headers)

        try {
            restTemplate.exchange("$BASE_URL$REGISTER_ENDPOINT", HttpMethod.PUT, request, String::class.java)
            fail()
        } catch (e: HttpClientErrorException) {
            if (e.statusCode != HttpStatus.BAD_REQUEST) {
                fail()
            }

            val validationErrors: List<ValidationError> = jacksonObjectMapper().readValue(e.responseBodyAsString)
            assert(validationErrors.contains(validationError))
        }
    }

    @Test
    fun createUserUnauthorized() {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED
        val body = LinkedMultiValueMap<String, String>()
        val request = HttpEntity(body, headers)

        try {
            restTemplate.exchange("$BASE_URL$REGISTER_ENDPOINT", HttpMethod.PUT, request, String::class.java)
            fail()
        } catch (e: HttpClientErrorException) {
            if (e.statusCode != HttpStatus.UNAUTHORIZED) {
                fail()
            }
        }
    }

    @DataProvider(name = "createUserMissingField")
    fun createUserMissingField(): MutableIterator<Array<Any>> {
        return arrayListOf(
                arrayOf(addBody(mapOf("username" to "username", "password" to "password")),
                        addError(100, "A value must be provided", listOf("EMAIL", "LAST_NAME", "FIRST_NAME", "DOB"))),
                arrayOf(addBody(mapOf("email" to "email", "password" to "password", "lastName" to "lastName", "firstName" to "firstName",
                        "username" to "username")),
                        addError(100, "A value must be provided", listOf("DOB")))
        ).iterator()
    }

    @Test(dataProvider = "unauthorizedTest")
    fun unauthorizedTest(url: String, httpMethod: HttpMethod) {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED
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
                arrayOf("$BASE_URL$AUTHENTICATE_ENDPOINT", HttpMethod.POST),
                arrayOf("$BASE_URL$USER_ENDPOINT", HttpMethod.DELETE)
        ).iterator()
    }

    private fun addBody(bodyInput: Map<String, String>): LinkedMultiValueMap<String, String> {
        val body = LinkedMultiValueMap<String, String>()
        bodyInput.forEach { (key, value) -> body.add(key, value) }
        return body
    }

    private fun addError(errorCode: Int, errorMessage: String, fields: List<String>): ValidationError {
        val validationError = ValidationError(errorCode, ValidationErrorDetails(errorMessage))
        validationError.details.fields.addAll(fields)
        return validationError
    }
}
