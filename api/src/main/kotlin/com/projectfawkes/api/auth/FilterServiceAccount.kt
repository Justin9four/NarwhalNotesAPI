package com.projectfawkes.api.auth

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.projectfawkes.api.dataClass.ServiceAccount
import com.projectfawkes.api.errorHandler.UnauthorizedException
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.bcrypt.BCrypt
import java.util.*
import javax.servlet.FilterChain
import javax.servlet.http.HttpFilter
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class FilterServiceAccount : HttpFilter() {
    private val logger: Logger = LogManager.getLogger()
    private val registeredServiceAccountsEnv = "RegisteredServiceAccounts"

    override fun doFilter(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        val serviceAccountsJSON = System.getenv(registeredServiceAccountsEnv)
        if (serviceAccountsJSON.isNullOrBlank()) logger.error("$registeredServiceAccountsEnv Environment Variable not set")
        try {
            val serviceAccounts: List<ServiceAccount> = jacksonObjectMapper().readValue(serviceAccountsJSON!!)
            val basicAuthEncoded = request.getHeader("Authorization")!!
            val decodedAuth = Base64.getDecoder().decode(basicAuthEncoded.drop(6)).toString(Charsets.US_ASCII)
            val accountNameAndPassword = decodedAuth.split(":")
            if (accountNameAndPassword.size != 2) {
                throw UnauthorizedException("Unauthenticated Service Account")
            }
            val serviceAccountHash = serviceAccounts.find { it.accountName == accountNameAndPassword[0] }!!.hash
            if (!BCrypt.checkpw(accountNameAndPassword[1], serviceAccountHash)) {
                throw UnauthorizedException("Unauthenticated Service Account")
            }
            SecurityContextHolder.getContext().authentication =
                UsernamePasswordAuthenticationToken(accountNameAndPassword[0], "verifiedServiceAccount")
        } catch (_: Exception) {
            // Service Account is unauthenticated.
        }
        chain.doFilter(request, response)
    }
}