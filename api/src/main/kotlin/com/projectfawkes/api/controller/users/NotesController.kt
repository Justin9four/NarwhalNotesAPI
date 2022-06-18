package com.projectfawkes.api.controller.users

import com.projectfawkes.api.auth.AuthType
import com.projectfawkes.api.auth.UseAuth
import com.projectfawkes.api.controller.NOTES_BY_ID_ENDPOINT
import com.projectfawkes.api.controller.NOTES_ENDPOINT
import com.projectfawkes.api.controller.NOTES_SYNC_PUSHER_ENDPOINT
import com.projectfawkes.api.controller.NOTES_UPLOAD_GOOGLE_DRIVE_ENDPOINT
import com.projectfawkes.api.dataClass.Note
import com.projectfawkes.api.errorHandler.*
import com.projectfawkes.api.service.createNote
import com.projectfawkes.api.service.deleteNote
import com.projectfawkes.api.service.getNotes
import com.projectfawkes.api.service.updateNote
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping(NOTES_ENDPOINT)
@UseAuth(AuthType.USER)
class NotesController {
    private val logger: Logger = LogManager.getLogger()

    val noteIDNotUnique = "Note ID is not unique"

    @PutMapping(NOTES_UPLOAD_GOOGLE_DRIVE_ENDPOINT)
    fun uploadGoogleDrive() = ResponseEntity<Void>(HttpStatus.OK)

    @PostMapping(NOTES_SYNC_PUSHER_ENDPOINT)
    fun usePusher() = ResponseEntity<Void>(HttpStatus.OK)

    @PutMapping
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

    @PostMapping
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

    @GetMapping(NOTES_BY_ID_ENDPOINT)
    fun getNoteById(request: HttpServletRequest, @PathVariable id: String): ResponseEntity<Note> {
        val creator = request.getAttribute("uid").toString()
        val notes = getNotes("id", id)
        when {
            notes.size > 1 -> throw DataConflictException(noteIDNotUnique)
            notes.isEmpty() -> throw DataNotFoundException("Note not found by id:$id")
            notes[0].creator != creator -> throw UnauthorizedException("Cannot query someone else's note")
        }
        return ResponseEntity(notes[0], HttpStatus.OK)
    }

    @GetMapping()
    fun getNotes(request: HttpServletRequest): ResponseEntity<List<Note>> {
        val creator = request.getAttribute("uid").toString()
        val notes = getNotes("creator", creator)
        return ResponseEntity(notes, HttpStatus.OK)
    }

    @DeleteMapping(NOTES_BY_ID_ENDPOINT)
    fun deleteNote(request: HttpServletRequest, @PathVariable id: String): ResponseEntity<Any> {
        val uid = request.getAttribute("uid").toString()
        val notes = getNotes("id", id)
        if (notes[0].creator != uid) throw UnauthorizedException("Cannot delete someone else's note")
        deleteNote(id)
        return ResponseEntity(HttpStatus.OK)
    }

}