package com.projectfawkes.api.authentication

import java.security.Permissions

// ROLE prefix is required by Spring on roles
enum class Roles(val value: String) {
    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN")
}

class Authenticator {
    // take a uid and see if the session is still active and the user has the required permission for
    // the function
    fun isAuthorized(uid: String, requiredPermissions: Permissions): Boolean {
        return true
    }
}