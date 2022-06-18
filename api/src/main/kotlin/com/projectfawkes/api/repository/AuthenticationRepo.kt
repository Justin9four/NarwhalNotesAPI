package com.projectfawkes.api.repository

import com.google.api.core.ApiFuture
import com.google.cloud.firestore.DocumentSnapshot
import com.google.cloud.firestore.QueryDocumentSnapshot
import com.projectfawkes.api.dataClass.Account
import com.projectfawkes.api.dataClass.Authentication
import com.projectfawkes.api.errorHandler.DataNotFoundException

class AuthenticationRepo : AccountRepo() {
    override fun getReturnObject(document: QueryDocumentSnapshot): Authentication {
        val roles = getRoles(document)
        val account = Account(document.id, document.get(Account::username.name).toString(), document.get(Account::email.name).toString(), document.get(Account::photoUrl.name).toString(), roles)
        return Authentication(account, document.get(Authentication::password.name).toString())
    }

    override fun getReturnObject(future: ApiFuture<DocumentSnapshot>): Authentication {
        if (!future.get().exists()) throw DataNotFoundException("Data not found")
        val roles = getRoles(future)
        // TODO implements this everywhere I can to eliminate literal strings
        val account = Account(future.get().id, future.get().get(Account::username.name).toString(), future.get().get(Account::email.name).toString(), future.get().get(Account::photoUrl.name).toString(), roles)
        return Authentication(account, future.get().get(Authentication::password.name).toString())
    }

    override fun getUniqueValuesCollection(): List<String> {
        return listOf(Account::username.name, Account::email.name)
    }
}