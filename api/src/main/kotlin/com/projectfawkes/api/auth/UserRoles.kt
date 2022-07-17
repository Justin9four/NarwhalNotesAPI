package com.projectfawkes.api.auth

// ROLE prefix is required by Spring on roles
enum class UserRoles(val value: String) {
    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN")
}