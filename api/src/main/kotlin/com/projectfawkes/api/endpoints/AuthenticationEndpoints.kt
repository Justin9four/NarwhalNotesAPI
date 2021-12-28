package com.projectfawkes.api.endpoints

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.SessionCookieOptions
import com.projectfawkes.api.API_ENDPOINT
import com.projectfawkes.api.AuthType
import com.projectfawkes.api.UseAuth
import com.projectfawkes.api.authentication.Roles
import com.projectfawkes.api.dataClasses.Account
import com.projectfawkes.api.dataClasses.Profile
import com.projectfawkes.api.errorHandler.Field
import com.projectfawkes.api.errorHandler.UnauthorizedException
import com.projectfawkes.api.errorHandler.Validator
import com.projectfawkes.api.models.authenticateCredentials
import com.projectfawkes.api.models.register
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus.OK
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.sql.Timestamp
import java.util.*
import java.util.concurrent.TimeUnit
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


const val AUTHENTICATE_ENDPOINT = "/authenticate"
const val REGISTER_ENDPOINT = "/register"
const val CHECK_TOKEN_ENDPOINT = "/checkToken"

@RestController
@RequestMapping(API_ENDPOINT)
@UseAuth(AuthType.SERVICEACCOUNT)
class AuthenticationEndpoints {
    private val logger: Logger = LogManager.getLogger()

    @PutMapping(REGISTER_ENDPOINT)
    fun register(@RequestBody body: Map<String, String>): ResponseEntity<Account> {
        logger.info("Inside /api/register")
        val values = Validator().validate(
            body,
            listOf(Field.EMAIL, Field.PASSWORD, Field.LAST_NAME, Field.FIRST_NAME, Field.USERNAME, Field.DOB)
        )

        val account = Account(
            null,
            values.getValue(Field.USERNAME),
            values.getValue(Field.EMAIL),
            null,
            listOf(Roles.USER.value)
        )
        val profile = Profile(
            null,
            values.getValue(Field.FIRST_NAME),
            values.getValue(Field.LAST_NAME),
            Timestamp(Date().time).toString(),
            values.getValue(Field.DOB)
        )
        val accountAndToken = register(account, profile, values.getValue(Field.PASSWORD))

        val headers = HttpHeaders()
        headers.add("x-auth-token", accountAndToken.token)
        return ResponseEntity(accountAndToken.account, headers, OK)
    }

    @PostMapping(AUTHENTICATE_ENDPOINT)
    fun authenticate(@RequestBody body: Map<String, String>): ResponseEntity<Account> {
        logger.info("Inside /api/authenticate")
        val usernameAndPassword = Validator().validate(body, listOf(Field.USERNAME, Field.PASSWORD))
        val accountAndToken = authenticateCredentials(
            usernameAndPassword.getValue(Field.USERNAME),
            usernameAndPassword.getValue(Field.PASSWORD)
        )

        val headers = HttpHeaders()
        headers.add("x-auth-token", accountAndToken.token)
        return ResponseEntity(accountAndToken.account, headers, OK)
    }

    @PostMapping(CHECK_TOKEN_ENDPOINT)
    fun checkToken(request: HttpServletRequest, response: HttpServletResponse): ResponseEntity<Any> {
        // To ensure that cookies are set only on recently signed in users, check auth_time in
        // ID token before creating a cookie.
        val idToken = request.getHeader("idToken")
        val decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken)
        val authTimeMillis = TimeUnit.SECONDS.toMillis(
            decodedToken.claims["auth_time"] as Long
        )

        // Only process if the user signed in within the last 5 minutes.
        if (System.currentTimeMillis() - authTimeMillis > TimeUnit.MINUTES.toMillis(5)) {
            throw UnauthorizedException("Token Expired")
        }

        return try {
            val expiresIn = TimeUnit.DAYS.toMillis(1)
            val options = SessionCookieOptions.builder()
                .setExpiresIn(expiresIn)
                .build()
            // Create the session cookie. This will also verify the ID token in the process.
            // The session cookie will have the same claims as the ID token.
            val sessionCookie = FirebaseAuth.getInstance().createSessionCookie(idToken, options)
            val cookie = Cookie("session", sessionCookie)
            cookie.path = "/"
            response.addCookie(cookie)
            // Set cookie policy parameters as required.
            ResponseEntity(OK)
        } catch (e: FirebaseAuthException) {
            throw UnauthorizedException("Failed to create a session cookie")
        }
    }
}
