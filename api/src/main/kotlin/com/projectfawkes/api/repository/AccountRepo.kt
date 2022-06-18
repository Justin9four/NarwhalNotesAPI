package com.projectfawkes.api.repository

import com.google.api.core.ApiFuture
import com.google.cloud.firestore.DocumentSnapshot
import com.google.cloud.firestore.QueryDocumentSnapshot
import com.projectfawkes.api.auth.Roles
import com.projectfawkes.api.dataClass.Account

open class AccountRepo : RepoBaseClass("accounts") {
    override fun getReturnObject(document: QueryDocumentSnapshot): Any {
        val roles = getRoles(document)
        return Account(document.id, document.get(Account::username.name).toString(), document.get(Account::email.name).toString(), document.get(Account::photoUrl.name).toString(), roles)
    }

    override fun getReturnObject(future: ApiFuture<DocumentSnapshot>): Any {
        val roles = getRoles(future)
        return Account(future.get().id, future.get().get(Account::username.name).toString(), future.get().get(Account::email.name).toString(), future.get().get(Account::photoUrl.name).toString(), roles)
    }

    override fun getUniqueValuesCollection(): List<String> {
        return listOf(Account::username.name, Account::email.name)
    }

    protected fun getRoles(future: ApiFuture<DocumentSnapshot>): List<String> {
        val roles: MutableList<String> = mutableListOf()
        if (future.get().get("isUser") == true) {
            roles.add(Roles.USER.value)
        }
        if (future.get().get("isAdmin") == true) {
            roles.add(Roles.ADMIN.value)
        }
        return roles
    }

    protected fun getRoles(document: QueryDocumentSnapshot): List<String> {
        val roles: MutableList<String> = mutableListOf()
        if (document.get("isUser") == true) {
            roles.add(Roles.USER.value)
        }
        if (document.get("isAdmin") == true) {
            roles.add(Roles.ADMIN.value)
        }
        return roles
    }
}