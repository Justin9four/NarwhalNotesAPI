package com.projectfawkes.api.security

import com.google.firebase.auth.FirebaseAuth
import com.projectfawkes.api.service.getAccount
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component

@Component
class AuthenticationProviderUser : AuthenticationProvider {
    override fun authenticate(auth: Authentication): Authentication {
        val uid = auth.principal.toString()
        val userRecord = FirebaseAuth.getInstance().getUser(uid)
        if (userRecord.isDisabled) throw DisabledException("Account disabled")
        val account = getAccount(uid)
        val authorities = account.roles?.map { SimpleGrantedAuthority(it) }
        return UsernamePasswordAuthenticationToken(uid, "", authorities)
    }

    override fun supports(authentication: Class<*>): Boolean {
        return authentication == UsernamePasswordAuthenticationToken::class.java
    }
}