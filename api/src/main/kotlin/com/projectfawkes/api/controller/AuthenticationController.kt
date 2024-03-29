package com.projectfawkes.api.controller

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.SessionCookieOptions
import com.projectfawkes.api.controller.dto.AuthenticateDto
import com.projectfawkes.api.controller.dto.RegisterDto
import com.projectfawkes.api.dataClass.Account
import com.projectfawkes.api.dataClass.Profile
import com.projectfawkes.api.errorHandler.UnauthorizedException
import com.projectfawkes.api.errorHandler.ValidationException
import com.projectfawkes.api.security.UserRoles
import com.projectfawkes.api.service.authenticateCredentials
import com.projectfawkes.api.service.register
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus.OK
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.util.WebUtils
import java.sql.Timestamp
import java.util.*
import java.util.concurrent.TimeUnit
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.validation.Valid

@RestController
@RequestMapping(API_ENDPOINT)
class AuthenticationController {
    @PostMapping(
        REGISTER_ENDPOINT,
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun register(@Valid @RequestBody registerDto: RegisterDto, errors: BindingResult): ResponseEntity<Account> {
        if (errors.hasErrors()) throw ValidationException(errors)
        val account = Account(
            null,
            registerDto.username,
            registerDto.email,
            null,
            listOf(UserRoles.USER.value)
        )
        val profile = Profile(
            null,
            registerDto.firstName,
            registerDto.lastName,
            Timestamp(Date().time).toString(),
            registerDto.dob
        )
        val accountAndTokenDto = register(account, profile, registerDto.password!!)

        val headers = HttpHeaders()
        headers.add("x-auth-token", accountAndTokenDto.token)
        return ResponseEntity(accountAndTokenDto.account, headers, OK)
    }

    @PostMapping(
        AUTHENTICATE_ENDPOINT,
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun authenticate(
        @Valid @RequestBody authenticateDto: AuthenticateDto,
        errors: BindingResult
    ): ResponseEntity<Account> {
        if (errors.hasErrors()) throw ValidationException(errors)
        val accountAndTokenDto = authenticateCredentials(
            authenticateDto.username!!,
            authenticateDto.password!!
        )

        val headers = HttpHeaders()
        headers.add("x-auth-token", accountAndTokenDto.token)
        return ResponseEntity(accountAndTokenDto.account, headers, OK)
    }

    @PostMapping(
        SIGN_OUT_ENDPOINT,
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun signOut(request: HttpServletRequest, response: HttpServletResponse): ResponseEntity<Any> {
        val sessionCookie = WebUtils.getCookie(request, "session")
        return try {
            val decodedToken = FirebaseAuth.getInstance().verifySessionCookie(sessionCookie?.value)
            FirebaseAuth.getInstance().revokeRefreshTokens(decodedToken.uid)
            val newCookie = Cookie("session", "")
            newCookie.maxAge = 0
            newCookie.path = "/"
            response.addCookie(newCookie)
            ResponseEntity(OK)
        } catch (e: FirebaseAuthException) {
            throw UnauthorizedException("Failed to create a session cookie")
        }
    }

    @PostMapping(
        CHECK_TOKEN_ENDPOINT,
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun checkToken(request: HttpServletRequest, response: HttpServletResponse): ResponseEntity<Any> {
        // To ensure that cookies are set only on recently signed-in users, check auth_time in
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
