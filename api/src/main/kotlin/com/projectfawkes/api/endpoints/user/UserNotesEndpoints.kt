package com.projectfawkes.api.endpoints.user

import com.projectfawkes.api.USER_ENDPOINT
import com.projectfawkes.api.dataClasses.Note
import com.projectfawkes.api.errorHandler.*
import com.projectfawkes.api.models.createNote
import com.projectfawkes.api.models.deleteNote
import com.projectfawkes.api.models.getNotes
import com.projectfawkes.api.models.updateNote
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

const val UPLOAD_GOOGLE_DRIVE_ENDPOINT = "/uploadGoogleDrive"
const val NOTE_ENDPOINT = "/note"
const val SYNC_PUSHER_ENDPOINT = "/pusher"

@RestController
@RequestMapping(USER_ENDPOINT)
class UserNotesEndpoints {
    private val logger: Logger = LogManager.getLogger()

    val noteIDNotUnique = "Note ID is not unique"

    @PutMapping(UPLOAD_GOOGLE_DRIVE_ENDPOINT)
    fun uploadGoogleDrive() = ResponseEntity<Void>(HttpStatus.OK)

    @PostMapping(SYNC_PUSHER_ENDPOINT)
    fun usePusher() = ResponseEntity<Void>(HttpStatus.OK)

    @PutMapping(NOTE_ENDPOINT)
    fun createNote(request: HttpServletRequest, @RequestBody body: Map<String, String>): ResponseEntity<Note> {
        val values = Validator(listOf(Field.TEXT)).validate(body, listOf(Field.TITLE, Field.TEXT))
        val uid = request.getAttribute("uid").toString()

        return ResponseEntity(
            createNote(
                values.getValue(Field.TITLE), uid, values[Field.TEXT]
                    ?: ""
            ), HttpStatus.OK
        )
    }

    @PostMapping(NOTE_ENDPOINT)
    fun updateNote(request: HttpServletRequest, @RequestBody body: Map<String, String>): ResponseEntity<Any> {
        val values =
            Validator(listOf(Field.TITLE, Field.TEXT)).validate(body, listOf(Field.TITLE, Field.TEXT, Field.ID))
        val uid = request.getAttribute("uid").toString()
        logger.info("Updating note by $uid with id ${values.getValue(Field.ID)}")

        val noteToUpdate = getNotes("id", values.getValue(Field.ID))[0]
        if (noteToUpdate.creator != uid) throw UnauthorizedException("Unauthorized to update other user's note")

        val note = Note(null, values[Field.TITLE], null, null, values[Field.TEXT])
        updateNote(values.getValue(Field.ID), note.convertToMap())
        return ResponseEntity(HttpStatus.OK)
    }

    @GetMapping(NOTE_ENDPOINT)
    fun getNotes(request: HttpServletRequest): ResponseEntity<List<Note>> {
        val creator = request.getAttribute("uid").toString()
        val id = request.getParameter("id")
        val field = if (!id.isNullOrBlank()) "id" else "creator"
        val value = id ?: creator
        val notes = getNotes(field, value)
        if (field == "id") {
            when {
                notes.size > 1 -> throw DataConflictException(noteIDNotUnique)
                notes.isEmpty() -> throw DataNotFoundException("Note not found by $field:$value")
                notes[0].creator != creator -> throw UnauthorizedException("Cannot query someone else's note")
            }
        }
        return ResponseEntity(notes, HttpStatus.OK)
    }

    @DeleteMapping(NOTE_ENDPOINT)
    fun deleteNote(request: HttpServletRequest, @RequestBody body: Map<String, String>): ResponseEntity<Any> {
        val id = body["id"]!!
        val uid = request.getAttribute("uid").toString()
        val notes = getNotes("id", id)
        if (notes[0].creator != uid) throw UnauthorizedException("Cannot delete someone else's note")
        deleteNote(id)
        return ResponseEntity(HttpStatus.OK)
    }

}