package com.projectfawkes.api.authentication

// ROLE prefix is required by Spring on roles
enum class Roles(val value: String) {
    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN")
}