package com.projectfawkes.api.data

import com.google.api.core.ApiFuture
import com.google.cloud.firestore.DocumentSnapshot
import com.google.cloud.firestore.QueryDocumentSnapshot
import com.projectfawkes.api.dataClasses.Profile

class ProfileRepo : RepoBaseClass("profiles") {
    override fun getReturnObject(document: QueryDocumentSnapshot): Profile {
        return Profile(document.id, document.get(Profile::firstName.name).toString(), document.get(Profile::lastName.name).toString(), document.get(Profile::createdDate.name).toString(), document.get(Profile::dob.name).toString())
    }

    override fun getReturnObject(future: ApiFuture<DocumentSnapshot>): Any {
        return Profile(future.get().id, future.get().get(Profile::firstName.name).toString(), future.get().get(Profile::lastName.name).toString(), future.get().get(Profile::createdDate.name).toString(), future.get().get(Profile::dob.name).toString())
    }

    override fun getUniqueValuesCollection(): List<String> {
        return listOf()
    }
}