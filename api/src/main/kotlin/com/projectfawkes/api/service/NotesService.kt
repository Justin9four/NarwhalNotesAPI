package com.projectfawkes.api.service

import com.projectfawkes.api.dataClass.Note
import com.projectfawkes.api.repository.NoteRepo
import java.time.ZonedDateTime
import java.util.*

private  val notesRepo = NoteRepo()
fun createNote(title: String, creator: String, text: String): Note {
    val id = UUID.randomUUID().toString()
    val date = ZonedDateTime.now().toString()

    val docData: MutableMap<String, Any> = HashMap()
    docData["title"] = title
    docData["creator"] = creator
    docData["date"] = date
    docData["text"] = text
    return notesRepo.create(id, docData) as Note
}

fun updateNote(id: String, updateData: Map<String, Any>) {
    // TODO add other search query for note creator uid and compare with session user ID. Can't edit someone else's note. maybe do at web layer and pass down as param
    notesRepo.update(id, updateData)    
}

fun getNotes(field: String, value: String): List<Note> {
    return notesRepo.getValues(field, value).filterIsInstance<Note>()
}

fun deleteNote(id: String) {
    // TODO add other search query for note creator uid and compare with session user ID. Can't edit someone else's note
    notesRepo.delete(id)
}