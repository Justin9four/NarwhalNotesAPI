package com.projectfawkes.utils

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.projectfawkes.AuthManager
import com.projectfawkes.BASE_URL
import com.projectfawkes.responseObjects.Note
import com.projectfawkes.responseObjects.UpdateNote
import com.projectfawkes.restTemplate
import org.springframework.http.*
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.util.UriComponentsBuilder

const val NOTE_ENDPOINT = "/note"

fun createNote(authManager: AuthManager, title: String, text: String? = null): String {
    val headers = HttpHeaders()
    headers.set("testUsername", authManager.uid)
    headers.contentType = MediaType.APPLICATION_FORM_URLENCODED
    val map: MultiValueMap<String, String> = LinkedMultiValueMap()
    map.add("title", title)
    if (text != null) map.add("text", text)
    val request = HttpEntity(map, headers)

    val response: ResponseEntity<String> =
        restTemplate.exchange("$BASE_URL$USER_ENDPOINT$NOTE_ENDPOINT", HttpMethod.PUT, request, String::class.java)
    val note: Note = jacksonObjectMapper().readValue(response.body ?: "")

    return note.id
}

fun getNoteById(authManager: AuthManager, id: String): List<Note> {
    val headers = HttpHeaders()
    headers.set("testUsername", authManager.uid)
    val request: HttpEntity<String> = HttpEntity(headers)
    val builder: UriComponentsBuilder = UriComponentsBuilder
        .fromHttpUrl("$BASE_URL$USER_ENDPOINT$NOTE_ENDPOINT")
        .queryParam("id", id)
    val response: ResponseEntity<String> =
        restTemplate.exchange(builder.toUriString(), HttpMethod.GET, request, String::class.java)
    return jacksonObjectMapper().readValue(response.body ?: "")
}

fun updateNote(authManager: AuthManager, updateNoteObject: UpdateNote): ResponseEntity<String> {
    val headers = HttpHeaders()
    headers.set("testUsername", authManager.uid)
    headers.contentType = MediaType.APPLICATION_FORM_URLENCODED
    val map: MultiValueMap<String, String> = LinkedMultiValueMap()

    map.add("id", updateNoteObject.id)
    if (!updateNoteObject.title.isNullOrBlank()) map.add("title", updateNoteObject.title)
    if (!updateNoteObject.text.isNullOrBlank()) map.add("text", updateNoteObject.text)

    val request = HttpEntity(map, headers)
    return restTemplate.exchange("$BASE_URL$USER_ENDPOINT$NOTE_ENDPOINT", HttpMethod.POST, request, String::class.java)
}

fun getNotesByCreator(authManager: AuthManager): List<Note> {
    val headers = HttpHeaders()
    headers.set("testUsername", authManager.uid)
    val request: HttpEntity<String> = HttpEntity(headers)
    val builder: UriComponentsBuilder = UriComponentsBuilder
        .fromHttpUrl("$BASE_URL$USER_ENDPOINT$NOTE_ENDPOINT")
    val response: ResponseEntity<String> =
        restTemplate.exchange(builder.toUriString(), HttpMethod.GET, request, String::class.java)

    return jacksonObjectMapper().readValue(response.body ?: "")
}

fun deleteNote(authManager: AuthManager, id: String): ResponseEntity<String> {
    val headers = HttpHeaders()
    headers.set("testUsername", authManager.uid)
    headers.contentType = MediaType.APPLICATION_FORM_URLENCODED
    val map: MultiValueMap<String, String> = LinkedMultiValueMap()
    map.add("id", id)
    val request = HttpEntity(map, headers)

    return restTemplate.exchange(
        "$BASE_URL$USER_ENDPOINT$NOTE_ENDPOINT",
        HttpMethod.DELETE,
        request,
        String::class.java
    )
}
