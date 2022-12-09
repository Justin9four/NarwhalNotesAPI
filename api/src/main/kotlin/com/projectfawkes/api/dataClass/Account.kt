package com.projectfawkes.api.dataClass

import com.projectfawkes.api.security.UserRoles

data class Account(var uid: String?, val username: String?, val email: String?, val photoUrl: String?, val roles: List<String>?) {
    fun getAccountMap(password: String?): Map<String, Any> {
        val accountMap = mutableMapOf<String, Any>()
        if (!username.isNullOrBlank()) accountMap["username"] = username
        if (!email.isNullOrBlank()) accountMap["email"] = email
        if (!photoUrl.isNullOrBlank()) accountMap["photoUrl"] = photoUrl
        if (!password.isNullOrBlank()) accountMap["password"] = password
        if (!roles.isNullOrEmpty()) {
            accountMap["isAdmin"] = roles.contains(UserRoles.ADMIN.value)
            if (roles.contains(UserRoles.USER.value)) accountMap["isUser"] = true
        }
        return accountMap
    }
}