package com.projectfawkes

import com.projectfawkes.utils.*
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.http.*
import org.springframework.web.client.HttpClientErrorException
import org.testng.annotations.AfterClass
import org.testng.annotations.BeforeClass
import org.testng.annotations.DataProvider
import org.testng.annotations.Test
import kotlin.test.fail

class NoteEndpointsFailureTest {
    private val logger: Logger = LogManager.getLogger()
    private var title = "noteTitle"
    private var creator = ""
    private var text = "This <b>is some </b> text!"

    private var username = "testUser9792"
    private val password = "testBabyYodaIsAwesome^2194ThisIsAPassword"
    private var username2 = "testUser4021"
    private var noteIdUser2 = ""

    @BeforeClass
    fun setUp() {
        testConnection()
        val account = createUser(
            username, password, "firstName", "lastName",
            "email@example.com", "12/12/1992"
        )
        creator = account.uid!!
        logger.info("Test user created: $creator username= $username")
        createUser(
            username2, password, "firstName", "lastName",
            "email1@example.com", "12/12/1992"
        )
        logger.info("Test user created: username= $username2")
        noteIdUser2 = createNote(username2, "title")
    }

    @AfterClass
    fun tearDown() {
        deleteUser(username)
        logger.info("Test user deleted: $username")
        deleteNote(username2, noteIdUser2)
        logger.info("Test note deleted: $noteIdUser2")
        deleteUser(username2)
        logger.info("Test user deleted: $username2")
    }

    @Test(dataProvider = "createNoteMissingField")
    fun createNoteMissingField(body: Map<String, String>) {
        val headers = HttpHeaders()
        headers.set("testUsername", username)
        headers.contentType = MediaType.APPLICATION_JSON
        val request = HttpEntity(body, headers)

        try {
            restTemplate.exchange("$BASE_URL$USER_ENDPOINT$NOTE_ENDPOINT", HttpMethod.PUT, request, String::class.java)
            fail()
        } catch (e: HttpClientErrorException) {
            if (e.statusCode != HttpStatus.BAD_REQUEST) {
                fail()
            }
        }
    }

    @Test
    fun createNoteUnauthorized() {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        val body = mutableMapOf("text" to text)
        val request = HttpEntity(body, headers)

        try {
            restTemplate.exchange("$BASE_URL$USER_ENDPOINT$NOTE_ENDPOINT", HttpMethod.PUT, request, String::class.java)
            fail()
        } catch (e: HttpClientErrorException) {
            if (e.statusCode != HttpStatus.UNAUTHORIZED) {
                fail()
            }
        }
    }

    @DataProvider(name = "createNoteMissingField")
    fun createNoteMissingField(): MutableIterator<Array<Any>> {
        return arrayListOf<Array<Any>>(
            arrayOf(mapOf("text" to text))
        ).iterator()
    }

    @Test
    fun updateOtherUsersNote() {
        val headers = HttpHeaders()
        headers.set("testUsername", username)
        headers.contentType = MediaType.APPLICATION_JSON
        val body = mutableMapOf("id" to noteIdUser2, "title" to "title-BIG")
        val request = HttpEntity(body, headers)

        try {
            restTemplate.exchange("$BASE_URL$USER_ENDPOINT$NOTE_ENDPOINT", HttpMethod.POST, request, String::class.java)
            fail()
        } catch (e: HttpClientErrorException) {
            if (e.statusCode != HttpStatus.UNAUTHORIZED) {
                fail()
            }
        }
    }

    @Test
    fun updateNoteByIdMissingId() {
        val headers = HttpHeaders()
        headers.set("testUsername", username)
        headers.contentType = MediaType.APPLICATION_JSON
        val body = mutableMapOf("title" to title, "text" to text)

        val request = HttpEntity(body, headers)
        try {
            restTemplate.exchange("$BASE_URL$USER_ENDPOINT$NOTE_ENDPOINT", HttpMethod.POST, request, String::class.java)
            fail()
        } catch (e: HttpClientErrorException) {
            if (e.statusCode != HttpStatus.BAD_REQUEST) {
                fail()
            }
        }
    }

    @Test
    fun deleteNoteMissingId() {
        val headers = HttpHeaders()
        headers.set("testUsername", username)
        headers.contentType = MediaType.APPLICATION_JSON
        val request = HttpEntity<String>(headers)

        try {
            restTemplate.exchange(
                "$BASE_URL$USER_ENDPOINT$NOTE_ENDPOINT",
                HttpMethod.DELETE,
                request,
                String::class.java
            )
        } catch (e: HttpClientErrorException) {
            if (e.statusCode != HttpStatus.METHOD_NOT_ALLOWED) {
                fail()
            }
        }
    }
}