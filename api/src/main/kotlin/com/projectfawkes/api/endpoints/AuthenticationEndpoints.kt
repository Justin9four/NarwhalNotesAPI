package com.projectfawkes.api.endpoints

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.SessionCookieOptions
import com.projectfawkes.api.API_ENDPOINT
import com.projectfawkes.api.BABY_YODA_HASH
import com.projectfawkes.api.SERVICE_ACCOUNT1_ID
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
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.OK
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.bcrypt.BCrypt
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.sql.Timestamp
import java.util.*
import java.util.concurrent.TimeUnit
import javax.annotation.Resource
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import kotlin.collections.ArrayList


const val AUTHENTICATE_ENDPOINT = "/authenticate"
const val REGISTER_ENDPOINT = "/register"
const val CHECK_TOKEN_ENDPOINT = "/checkToken"

@RestController
@RequestMapping(API_ENDPOINT)
class AuthenticationEndpoints {
    private val logger: Logger = LogManager.getLogger()

    @PutMapping(REGISTER_ENDPOINT)
    fun register(requestBody: HttpServletRequest): ResponseEntity<Account> {
        logger.info("Inside /api/register")
        val values = Validator().validate(requestBody, listOf(Field.EMAIL, Field.PASSWORD, Field.LAST_NAME, Field.FIRST_NAME, Field.USERNAME, Field.DOB))

        val account = Account(null,
                values.getValue(Field.USERNAME),
                values.getValue(Field.EMAIL),
                null,
                listOf(Roles.USER.value))
        val profile = Profile(null,
                values.getValue(Field.FIRST_NAME),
                values.getValue(Field.LAST_NAME),
                Timestamp(Date().time).toString(),
                values.getValue(Field.DOB))
        val accountAndToken = register(account, profile, values.getValue(Field.PASSWORD))

        val headers = HttpHeaders()
        headers.add(HttpHeaders.SET_COOKIE, accountAndToken.token)
        return ResponseEntity(accountAndToken.account, headers, OK)
    }

    @PostMapping(AUTHENTICATE_ENDPOINT)
    fun authenticate(requestBody: HttpServletRequest): ResponseEntity<Account> {
        logger.info("Inside /api/authenticate")
        val usernameAndPassword = Validator().validate(requestBody, listOf(Field.USERNAME, Field.PASSWORD))
        val accountAndToken = authenticateCredentials(usernameAndPassword.getValue(Field.USERNAME), usernameAndPassword.getValue(Field.PASSWORD))

        val headers = HttpHeaders()
        headers.add(HttpHeaders.SET_COOKIE, accountAndToken.token)
        return ResponseEntity(accountAndToken.account, headers, OK)
    }

    @PostMapping(CHECK_TOKEN_ENDPOINT)
    fun checkToken(request: HttpServletRequest, response: HttpServletResponse): ResponseEntity<Any> {
        // To ensure that cookies are set only on recently signed in users, check auth_time in
        // ID token before creating a cookie.
        val idToken = request.getHeader("idToken")
        val decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
        val authTimeMillis = TimeUnit.SECONDS.toMillis(
                decodedToken.claims["auth_time"] as Long);

        // Only process if the user signed in within the last 5 minutes.
        if (System.currentTimeMillis() - authTimeMillis > TimeUnit.MINUTES.toMillis(5)) {
            throw UnauthorizedException("Token Expired")
        }

        return try {
            val expiresIn = TimeUnit.DAYS.toMillis(1);
            val options = SessionCookieOptions.builder()
                    .setExpiresIn(expiresIn)
                    .build();
            // Create the session cookie. This will also verify the ID token in the process.
            // The session cookie will have the same claims as the ID token.
            val sessionCookie = FirebaseAuth.getInstance().createSessionCookie(idToken, options)
            response.addCookie(Cookie("session", sessionCookie))
            // Set cookie policy parameters as required.
            ResponseEntity(OK)
        } catch (e: FirebaseAuthException) {
            throw UnauthorizedException("Failed to create a session cookie")
        }
    }

    private fun authenticateServiceAccount(requestBody: HttpServletRequest) {
        val serviceAccountId = requestBody.getParameter("serviceAccountId")
        val key = requestBody.getParameter("key")
        if (serviceAccountId.isNullOrBlank() || key.isNullOrBlank()) {
            throw UnauthorizedException("Unauthenticated. Username and Password must be provided")
        }
        // TODO eventually get service account info from env variable. I can add a long private encrypted key
        val hash = BABY_YODA_HASH
        val expectedId = SERVICE_ACCOUNT1_ID
        if (!BCrypt.checkpw(key, hash) && serviceAccountId != expectedId) {
            throw UnauthorizedException("Unauthenticated. Username or Password incorrect")
        }
    }

    private fun addUserToSpringSecurity(uid: String, password: String) {
        // TODO TODO add correct authorities based on database
        //  actually is this unnecessary now that I'm handling
        //  authentication through Google Firebase auth???
        val authorities: MutableList<GrantedAuthority> = ArrayList()
        authorities.add(GrantedAuthority { Roles.USER.value })
        val userWithAuthorities: UserDetails = org.springframework.security.core.userdetails.User(uid, "{bcrypt}$password", authorities)
        // PARAHACER save the authorities somewhere https://stackoverflow.com/questions/32244745/how-to-add-new-user-to-spring-security-at-runtime
        val authentication: Authentication = UsernamePasswordAuthenticationToken(userWithAuthorities, null, authorities)
        SecurityContextHolder.getContext().authentication = authentication
        logger.info("Principal: " + SecurityContextHolder.getContext().authentication.principal)
        logger.info("name: " + SecurityContextHolder.getContext().authentication.name)
        logger.info("Details: " + SecurityContextHolder.getContext().authentication.details)
        // PARAHACER pass more information like whole user object to the session
        // Maybe add it to the session scope https://www.baeldung.com/spring-bean-scopes
        // add user at web layer to session
    }

}
