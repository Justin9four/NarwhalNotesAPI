package com.projectfawkes.api.data

import com.google.api.core.ApiFuture
import com.google.cloud.firestore.DocumentSnapshot
import com.google.cloud.firestore.QueryDocumentSnapshot
import com.projectfawkes.api.dataClasses.Account
import com.projectfawkes.api.dataClasses.Note

class NoteRepo : RepoBaseClass("notes") {
    override fun getReturnObject(document: QueryDocumentSnapshot): Note {
        return Note(document.id, document.get("title").toString(), document.get("creator").toString(), document.get("createdDate").toString(), document.get("text").toString())
    }

    override fun getReturnObject(future: ApiFuture<DocumentSnapshot>): Note {
        return Note(future.get().id, future.get().get("title").toString(), future.get().get("creator").toString(), future.get().get("createdDate").toString(), future.get().get("text").toString())
    }

    override fun getUniqueValuesCollection(): List<String> {
        return listOf()
    }
}