package com.projectfawkes.api.security

import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component

@Component
class AuthenticationProviderServiceAccount : AuthenticationProvider {
    override fun authenticate(auth: Authentication): Authentication {
        val authorities = listOf(SimpleGrantedAuthority("ROLE_SERVICE_ACCOUNT"))
        return UsernamePasswordAuthenticationToken(auth.principal, "", authorities)
    }

    override fun supports(authentication: Class<*>): Boolean {
        return authentication == UsernamePasswordAuthenticationToken::class.java
    }
}