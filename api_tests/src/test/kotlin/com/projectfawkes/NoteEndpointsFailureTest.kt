package com.projectfawkes

import com.projectfawkes.utils.*
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.http.*
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
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
    fun createNoteMissingField(body: LinkedMultiValueMap<String, String>) {
        val headers = HttpHeaders()
        headers.set("testUsername", username)
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED
        val map: MultiValueMap<String, String> = body
        val request = HttpEntity(map, headers)

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
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED
        val map: MultiValueMap<String, String> = LinkedMultiValueMap()
        map.add("text", text)
        val request = HttpEntity(map, headers)

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
                arrayOf(addBody(mapOf("text" to text)))
        ).iterator()
    }

    @Test
    fun updateOtherUsersNote() {
        val headers = HttpHeaders()
        headers.set("testUsername", username)
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED
        val map: MultiValueMap<String, String> = LinkedMultiValueMap()
        map.add("id", noteIdUser2)
        map.add("title", "title-BIG")
        val request = HttpEntity(map, headers)

        //
//        val map: MultiValueMap<String, String> = LinkedMultiValueMap()
//
//        map.add("id", updateNoteObject.id)
//        if (!updateNoteObject.title.isNullOrBlank()) map.add("title", updateNoteObject.title)
//        if (!updateNoteObject.text.isNullOrBlank()) map.add("text", updateNoteObject.text)
//
//        val request = HttpEntity(map, headers)
//        return restTemplate.exchange("$BASE_URL$USER_ENDPOINT$NOTE_ENDPOINT", HttpMethod.POST, request, String::class.java)

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
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED
        val map: MultiValueMap<String, String> = LinkedMultiValueMap()

        map.add("title", title)
        map.add("text", text)

        val request = HttpEntity(map, headers)
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
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED
        val map: MultiValueMap<String, String> = LinkedMultiValueMap()
        val request = HttpEntity(map, headers)

        try {
            restTemplate.exchange("$BASE_URL$USER_ENDPOINT$NOTE_ENDPOINT", HttpMethod.DELETE, request, String::class.java)
        } catch (e: HttpClientErrorException) {
            if (e.statusCode != HttpStatus.BAD_REQUEST) {
                fail()
            }
        }
    }

    private fun addBody(bodyInput: Map<String, String>): LinkedMultiValueMap<String, String> {
        val body = LinkedMultiValueMap<String, String>()
        bodyInput.forEach { (key, value) -> body.add(key, value) }
        return body
    }
}