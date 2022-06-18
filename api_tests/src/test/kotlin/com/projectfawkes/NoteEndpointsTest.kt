package com.projectfawkes

import com.projectfawkes.responseObjects.Note
import com.projectfawkes.responseObjects.UpdateNote
import com.projectfawkes.utils.*
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException
import org.testng.annotations.AfterClass
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue
import kotlin.test.fail

class NoteEndpointsTest {
    private val logger: Logger = LogManager.getLogger()

    private var ids: MutableList<String> = ArrayList()
    private var title = "noteTitle"
    private var creator = ""
    private var text = "This <b>is some </b> text!"
    private var title2 = "noteTitle2"
    private var text2 = ""

    private var username = "testUser9752"
    private val password = "testBabyYodaIsAwesome^2194ThisIsAPassword"

    @BeforeClass
    fun setUp() {
        testConnection()
        val account = createUser(
            username, password, "firstName", "lastName",
            "email@example.com", "12/12/1992"
        )
        creator = account.uid!!
        logger.info("Test user created: $creator username= $username")
    }

    @AfterClass
    fun tearDown() {
        deleteUser(username)
        logger.info("Test user deleted: $username")
    }

    @Test
    fun createNoteSuccess() {
        ids.add(createNote(username, title, text))

        assertNotEquals(ids[0], "")
    }

    @Test(dependsOnMethods = ["createNoteSuccess"], priority = 1)
    fun getNoteByIdSuccess() {
        val note = getNoteById(username, ids[0])

        assertEquals(title, note.title)
        assertEquals(creator, note.creator)
        assertEquals(text, note.text)
    }

    @Test(dependsOnMethods = ["createNoteSuccess"], priority = 2)
    fun updateNoteByIdSuccess() {
        text = "Some other random text"
        val noteToUpdate = UpdateNote(id = ids[0], text = text)

        val response = updateNote(username, noteToUpdate)
        assertEquals(response.statusCode, HttpStatus.OK)
    }

    @Test(dependsOnMethods = ["createNoteSuccess"], priority = 3)
    fun createSecondNoteSuccess() {
        ids.add(createNote(username, title2))

        assertNotEquals(ids[1], "")
    }

    @Test(dependsOnMethods = ["createNoteSuccess"], priority = 4)
    fun getNotesByCreatorSuccess() {
        val notes = getNotesByCreator(username)
        logger.info("User $creator notes: $notes")

        assertEquals(notes.size, ids.size, "Returned incorrect number of notes")
        assertTrue(containsNote(notes, title, creator, text), "Note not found in creators notes")
        assertTrue(containsNote(notes, title2, creator, text2), "Note not found in creators notes")
    }

    @Test(dependsOnMethods = ["createNoteSuccess"], priority = 5)
    fun deleteNotesSuccess() {
        logger.info("size " + ids.size)
        for (id in ids) {
            val response = deleteNote(username, id)
            assertEquals(response.statusCode, HttpStatus.OK)

            // get the user to determine if deleted
            try {
                getNoteById(username, id)
                fail()
            } catch (e: HttpClientErrorException) {
                if (e.statusCode == HttpStatus.NOT_FOUND) {
                    logger.info("Deleted note with id: $id")
                } else {
                    fail("Failed to delete note with id: $id")
                }
            }
        }
    }

    private fun containsNote(notes: List<Note>, title: String, creator: String, text: String): Boolean {
        var containsNote = false
        for (note in notes) {
            if (note.title == title && note.creator == creator && note.text == text) {
                containsNote = true
            }
        }
        return containsNote
    }
}