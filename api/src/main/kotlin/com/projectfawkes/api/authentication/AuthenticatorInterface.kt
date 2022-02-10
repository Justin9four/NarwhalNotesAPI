package com.projectfawkes.api.authentication

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.firebase.auth.FirebaseAuth
import com.projectfawkes.api.dataClasses.Account
import com.projectfawkes.api.dataClasses.ServiceAccount
import com.projectfawkes.api.errorHandler.UnauthorizedException
import com.projectfawkes.api.models.getAccount
import org.apache.logging.log4j.Logger
import org.springframework.security.crypto.bcrypt.BCrypt
import org.springframework.web.util.WebUtils
import java.util.*
import javax.servlet.http.HttpServletRequest


interface AuthenticatorInterface {
    val logger: Logger
    val registeredServiceAccountsEnv: String
    val host: String

    fun getUidFromSession(request: HttpServletRequest): String {
        // TIP for client. Might need to do some CSRF work
        val sessionCookie: String? = WebUtils.getCookie(request, "session")?.value
        return try {
            // Verify the session cookie. In this case an additional check is added to detect
            // if the user's Firebase session was revoked, user deleted/disabled, etc.
            val checkRevoked = true
            val decodedToken = FirebaseAuth.getInstance().verifySessionCookie(
                sessionCookie, checkRevoked
            )
            decodedToken.uid
        } catch (e: Exception) {
            // Session cookie is unavailable, invalid or revoked. Force user to login.
            throw UnauthorizedException("Session Cookie Invalid")
        }
    }

    fun authenticateSession(request: HttpServletRequest): Account {
        val uid: String = getUidFromSession(request)
        val userRecord = FirebaseAuth.getInstance().getUser(uid)
        if (userRecord.isDisabled) throw UnauthorizedException("Account disabled")
        val account = getAccount(uid)
        if (!account.roles!!.contains(Roles.USER.value)) {
            throw UnauthorizedException("Session Cookie Invalid")
        }
        request.setAttribute("uid", uid)
        return account
    }

    fun authenticateServiceAccount(request: HttpServletRequest) {
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
        } catch (e: NullPointerException) {
            throw UnauthorizedException("Unauthenticated Service Account")
        } catch (e: IllegalArgumentException) {
            throw UnauthorizedException("Unauthenticated Service Account")
        }
    }
}