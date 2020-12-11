package com.projectfawkes.api.authentication

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.firebase.auth.FirebaseAuth
import com.projectfawkes.api.API_ENDPOINT
import com.projectfawkes.api.USER_ENDPOINT
import com.projectfawkes.api.dataClasses.ServiceAccount
import com.projectfawkes.api.endpoints.AUTHENTICATE_ENDPOINT
import com.projectfawkes.api.endpoints.REGISTER_ENDPOINT
import com.projectfawkes.api.endpoints.admin.USERS_ENDPOINT
import com.projectfawkes.api.endpoints.user.NOTE_ENDPOINT
import com.projectfawkes.api.errorHandler.UnauthorizedException
import com.projectfawkes.api.models.getAccountByUsername
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.security.crypto.bcrypt.BCrypt
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.util.WebUtils
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

// TODO check Admin and User claims for permissions
class MyInterceptor : HandlerInterceptor {
    private val logger: Logger = LogManager.getLogger()
    private val registeredServiceAccountsEnv = "RegisteredServiceAccounts"

    private fun getTestUidOrNull(testUsername: String?): String? {
        logger.info("Test Username: $testUsername")
        if (testUsername.isNullOrBlank()) {
            return null
        }
        if (testUsername.startsWith("test", ignoreCase = true)) {
            val account = getAccountByUsername(testUsername)
            return account.uid
        }
        return null
    }

    private fun getUidFromSessionCookie(sessionCookie: String?): String {
        logger.info("TODO Verify ID Token")
        // TIP for client. Might need to do some CSRF work
        return try {
            // Verify the session cookie. In this case an additional check is added to detect
            // if the user's Firebase session was revoked, user deleted/disabled, etc.
            val checkRevoked = true
            val decodedToken = FirebaseAuth.getInstance().verifySessionCookie(
                sessionCookie, checkRevoked)
            decodedToken.uid
        } catch (e: Exception) {
            // Session cookie is unavailable, invalid or revoked. Force user to login.
            throw UnauthorizedException("Session Cookie Invalid")
        }
    }

    private fun authenticateSession(request: HttpServletRequest) {
        val sessionCookie: String? = WebUtils.getCookie(request, "session")?.value
        val uid: String = getTestUidOrNull(request.getHeader("testUsername"))
            ?: getUidFromSessionCookie(sessionCookie)
        request.setAttribute("uid", uid)
    }

    private fun authenticateServiceAccount(request: HttpServletRequest) {
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
            val serviceAccountHash = serviceAccounts.find{it.accountName == accountNameAndPassword[0] }!!.hash
            if (!BCrypt.checkpw(accountNameAndPassword[1], serviceAccountHash)) {
                throw UnauthorizedException("Unauthenticated Service Account")
            }
        } catch (e: NullPointerException) {
            throw UnauthorizedException("Unauthenticated Service Account")
        }
    }

    private fun uriMatchesEndpoint(endpointWhitelist: List<String>, uri: String): Boolean {
        return endpointWhitelist.any{ it == uri || "$it/" == uri}
    }

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val endpointsWithUserAuth = listOf(USER_ENDPOINT, USER_ENDPOINT + NOTE_ENDPOINT, USERS_ENDPOINT)
        val endpointsWithServiceAccountAuth = listOf("$API_ENDPOINT$REGISTER_ENDPOINT", "$API_ENDPOINT$AUTHENTICATE_ENDPOINT")
        logger.info("${request.method} ${request.requestURI}")
        if (uriMatchesEndpoint(endpointsWithUserAuth, request.requestURI)) {
            authenticateSession(request)
        }
        else if (uriMatchesEndpoint(endpointsWithServiceAccountAuth, request.requestURI)) {
            // TODO implement Service Account Authentication
            authenticateServiceAccount(request)
            logger.info("Service Account Authentication not implemented")
        }
        return true
    }
}
