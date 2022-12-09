package com.projectfawkes.api.security

import com.google.firebase.auth.FirebaseAuth
import com.projectfawkes.api.service.getAccountByUsername
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.util.WebUtils
import javax.servlet.FilterChain
import javax.servlet.http.HttpFilter
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class FilterSession : HttpFilter() {
    private val logger: Logger = LogManager.getLogger()
    private val isMockSessionEnv = System.getenv("spring_profiles_active").equals("dev")

    // Non-Production local environments bypass session for test prefix
    private fun setTestAuthentication(request: HttpServletRequest) {
        val testUsername = request.getHeader("testUsername")
        logger.info("Test Username: $testUsername")
        if (testUsername.isNullOrBlank()) {
            return
        }
        if (testUsername.isNotBlank() && testUsername.startsWith("test", ignoreCase = true)) {
            val account = getAccountByUsername(testUsername)
            SecurityContextHolder.getContext().authentication =
                UsernamePasswordAuthenticationToken(account.uid, "verifiedToken")
        }
    }

    override fun doFilter(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        if (isMockSessionEnv) setTestAuthentication(request)
        // TIP for client. Might need to do some CSRF work
        val sessionCookie: String = WebUtils.getCookie(request, "session")?.value ?: ""
        try {
            // Verify the session cookie. In this case an additional check is added to detect
            // if the user's Firebase session was revoked, user deleted/disabled, etc.
            val checkRevoked = true
            val decodedToken = FirebaseAuth.getInstance().verifySessionCookie(
                sessionCookie, checkRevoked
            )
            SecurityContextHolder.getContext().authentication =
                UsernamePasswordAuthenticationToken(decodedToken.uid, "verifiedToken")
        } catch (_: Exception) {
            // Session cookie is unavailable, invalid or revoked.
        }
        chain.doFilter(request, response)
    }
}