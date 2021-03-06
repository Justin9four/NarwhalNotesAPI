package com.projectfawkes.api.dataClasses

import com.projectfawkes.api.authentication.Roles

data class Account(var uid: String?, val username: String?, val email: String?, val photoUrl: String?, val roles: List<String>?) {
    fun getAccountMap(password: String?): Map<String, Any> {
        val accountMap = mutableMapOf<String, Any>()
        if (!username.isNullOrBlank()) accountMap["username"] = username
        if (!email.isNullOrBlank()) accountMap["email"] = email
        if (!photoUrl.isNullOrBlank()) accountMap["photoUrl"] = photoUrl
        if (!password.isNullOrBlank()) accountMap["password"] = password
        if (!roles.isNullOrEmpty()) {
            accountMap["isAdmin"] = roles.contains(Roles.ADMIN.value)
            if (roles.contains(Roles.USER.value)) accountMap["isUser"] = true
        }
        return accountMap
    }
}