package com.projectfawkes.api

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class UseAuth(val authType: AuthType)

enum class AuthType {
    SERVICEACCOUNT, ADMIN, USER, PUBLIC
}
