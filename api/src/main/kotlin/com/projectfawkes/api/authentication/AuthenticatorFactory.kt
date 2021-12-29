package com.projectfawkes.api.authentication

fun authenticatorFactory() = when (System.getenv("spring_profiles_active")) {
    "dev" -> AuthenticatorDev()
    "production" -> Authenticator()
    else -> Authenticator()
}