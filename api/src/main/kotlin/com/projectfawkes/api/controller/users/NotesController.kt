package com.projectfawkes.api.controller.users

import com.projectfawkes.api.controller.NOTES_BY_ID_ENDPOINT
import com.projectfawkes.api.controller.NOTES_ENDPOINT
import com.projectfawkes.api.controller.NOTES_SYNC_PUSHER_ENDPOINT
import com.projectfawkes.api.controller.NOTES_UPLOAD_GOOGLE_DRIVE_ENDPOINT
import com.projectfawkes.api.controller.dto.CreateNoteDto
import com.projectfawkes.api.controller.dto.UpdateNoteDto
import com.projectfawkes.api.dataClass.Note
import com.projectfawkes.api.errorHandler.DataConflictException
import com.projectfawkes.api.errorHandler.DataNotFoundException
import com.projectfawkes.api.errorHandler.UnauthorizedException
import com.projectfawkes.api.errorHandler.ValidationException
import com.projectfawkes.api.service.createNote
import com.projectfawkes.api.service.deleteNote
import com.projectfawkes.api.service.getNotes
import com.projectfawkes.api.service.updateNote
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest
import javax.validation.Valid

@RestController
@RequestMapping(NOTES_ENDPOINT)
class NotesController {
    val noteIDNotUnique = "Note ID is not unique"

    @PutMapping(NOTES_UPLOAD_GOOGLE_DRIVE_ENDPOINT)
    fun uploadGoogleDrive() = ResponseEntity<Void>(HttpStatus.OK)

    @PostMapping(NOTES_SYNC_PUSHER_ENDPOINT)
    fun usePusher() = ResponseEntity<Void>(HttpStatus.OK)

    @PostMapping
    fun createNote(
        request: HttpServletRequest,
        @Valid @RequestBody createNoteDto: CreateNoteDto,
        errors: BindingResult
    ): ResponseEntity<Note> {
        if (errors.hasErrors()) throw ValidationException(errors)
        val uid = SecurityContextHolder.getContext().authentication.principal.toString()

        return ResponseEntity(
            createNote(
                createNoteDto.title!!, uid, createNoteDto.text
                    ?: ""
            ), HttpStatus.OK
        )
    }

    @PutMapping
    fun updateNote(
        request: HttpServletRequest,
        @Valid @RequestBody updateNoteDto: UpdateNoteDto,
        errors: BindingResult
    ): ResponseEntity<Any> {
        if (errors.hasErrors()) throw ValidationException(errors)
        val uid = SecurityContextHolder.getContext().authentication.principal.toString()

        val noteToUpdate = getNotes("id", updateNoteDto.id!!)[0]
        if (noteToUpdate.creator != uid) throw UnauthorizedException("Unauthorized to update other user's note")

        val note = Note(null, updateNoteDto.title, null, null, updateNoteDto.text)
        updateNote(updateNoteDto.id, note.convertToMap())
        return ResponseEntity(HttpStatus.OK)
    }

    @GetMapping(NOTES_BY_ID_ENDPOINT)
    fun getNoteById(request: HttpServletRequest, @PathVariable id: String): ResponseEntity<Note> {
        val creator = SecurityContextHolder.getContext().authentication.principal.toString()
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
        val creator = SecurityContextHolder.getContext().authentication.principal.toString()
        val notes = getNotes("creator", creator)
        return ResponseEntity(notes, HttpStatus.OK)
    }

    @DeleteMapping(NOTES_BY_ID_ENDPOINT)
    fun deleteNote(request: HttpServletRequest, @PathVariable id: String): ResponseEntity<Any> {
        val uid = SecurityContextHolder.getContext().authentication.principal.toString()
        val notes = getNotes("id", id)
        if (notes[0].creator != uid) throw UnauthorizedException("Cannot delete someone else's note")
        deleteNote(id)
        return ResponseEntity(HttpStatus.OK)
    }

}