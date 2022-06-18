package com.projectfawkes.utils

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.projectfawkes.BASE_URL
import com.projectfawkes.api.controller.NOTES_ENDPOINT
import com.projectfawkes.responseObjects.Note
import com.projectfawkes.responseObjects.UpdateNote
import com.projectfawkes.restTemplate
import org.springframework.http.*
import org.springframework.web.util.UriComponentsBuilder

fun createNote(username: String, title: String, text: String? = null): String {
    val headers = HttpHeaders()
    headers.set("testUsername", username)
    headers.contentType = MediaType.APPLICATION_JSON
    val body = mutableMapOf("title" to title)
    if (text != null) body["text"] = text
    val request = HttpEntity(body, headers)

    val response: ResponseEntity<String> =
        restTemplate.exchange("$BASE_URL$NOTES_ENDPOINT", HttpMethod.PUT, request, String::class.java)
    val note: Note = jacksonObjectMapper().readValue(response.body ?: "")

    return note.id
}

fun getNoteById(username: String, id: String): Note {
    val headers = HttpHeaders()
    headers.set("testUsername", username)
    val request: HttpEntity<String> = HttpEntity(headers)
    val builder: UriComponentsBuilder = UriComponentsBuilder
        .fromHttpUrl("$BASE_URL$NOTES_ENDPOINT/$id")
    val response: ResponseEntity<String> =
        restTemplate.exchange(builder.toUriString(), HttpMethod.GET, request, String::class.java)
    return jacksonObjectMapper().readValue(response.body ?: "")
}

fun updateNote(username: String, updateNoteObject: UpdateNote): ResponseEntity<String> {
    val headers = HttpHeaders()
    headers.set("testUsername", username)
    headers.contentType = MediaType.APPLICATION_JSON
    val body = mutableMapOf("id" to updateNoteObject.id)

    if (!updateNoteObject.title.isNullOrBlank()) body["title"] = updateNoteObject.title!!
    if (!updateNoteObject.text.isNullOrBlank()) body["text"] = updateNoteObject.text!!

    val request = HttpEntity(body, headers)
    return restTemplate.exchange("$BASE_URL$NOTES_ENDPOINT", HttpMethod.POST, request, String::class.java)
}

fun getNotesByCreator(username: String): List<Note> {
    val headers = HttpHeaders()
    headers.set("testUsername", username)
    val request: HttpEntity<String> = HttpEntity(headers)
    val response: ResponseEntity<String> =
        restTemplate.exchange("$BASE_URL$NOTES_ENDPOINT", HttpMethod.GET, request, String::class.java)

    return jacksonObjectMapper().readValue(response.body ?: "")
}

fun deleteNote(username: String, id: String): ResponseEntity<String> {
    val headers = HttpHeaders()
    headers.set("testUsername", username)
    headers.contentType = MediaType.APPLICATION_JSON
    val request: HttpEntity<String> = HttpEntity(headers)

    return restTemplate.exchange(
        "$BASE_URL$NOTES_ENDPOINT/$id",
        HttpMethod.DELETE,
        request,
        String::class.java
    )
}
