package com.projectfawkes.api.authentication

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.projectfawkes.api.API_ENDPOINT
import com.projectfawkes.api.USER_ENDPOINT
import com.projectfawkes.api.endpoints.user.NOTE_ENDPOINT
import com.projectfawkes.api.errorHandler.UnauthorizedException
import com.projectfawkes.api.models.getAccountByUsername
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.authentication.HttpStatusEntryPoint
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.session.ReactiveMapSessionRepository
import org.springframework.session.ReactiveSessionRepository
import org.springframework.session.config.annotation.web.server.EnableSpringWebSession
import org.springframework.session.data.redis.config.annotation.web.server.EnableRedisWebSession
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.util.WebUtils
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Configuration
@EnableSpringWebSession
class SessionConfig {
    @Bean
    fun reactiveSessionRepository(): ReactiveSessionRepository<*> {
        return ReactiveMapSessionRepository(ConcurrentHashMap())
    }
}

@Configuration
@EnableRedisWebSession
class RedisConfig {
    @Bean
    fun redisConnectionFactory(): LettuceConnectionFactory {
        return LettuceConnectionFactory()
    }
}

// TODO check Admin and User claims for permissions
class IdTokenInterceptor : HandlerInterceptor {
    private val logger: Logger = LogManager.getLogger()

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

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val endpointsWithUserAuth = listOf(USER_ENDPOINT, USER_ENDPOINT + NOTE_ENDPOINT)
        if (endpointsWithUserAuth.contains(request.requestURI)) {
            logger.info("URI needs Auth")
            val sessionCookie: String? = WebUtils.getCookie(request, "session")?.value
            val uid: String = getTestUidOrNull(request.getHeader("testUsername"))
                    ?: getUidFromSessionCookie(sessionCookie)
            request.setAttribute("uid", uid)
        }
        return true
    }
}

@Configuration
class RequestHandler : WebMvcConfigurer {
    override fun addInterceptors(registry: InterceptorRegistry) {
        super.addInterceptors(registry)
        registry.addInterceptor(IdTokenInterceptor())
    }
}

@Component
class RestAuthenticationEntryPoint : AuthenticationEntryPoint {
    private val logger: Logger = LogManager.getLogger()

    @Throws(IOException::class)
    override fun commence(
            request: HttpServletRequest?,
            response: HttpServletResponse,
            authException: AuthenticationException?) {
        logger.info("in RestAuthenticationEntryPoint")
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                "Unauthorized")
    }
}

@Configuration
@EnableWebSecurity
class SecurityConfig : WebSecurityConfigurerAdapter() {

    // hasRole() must have the role name without the "ROLE" word (Spring rule)
    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http
                .csrf().disable()
                .exceptionHandling()
                .authenticationEntryPoint(HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)).and()
                .httpBasic().and()
                .authorizeRequests()
                .antMatchers("/api/user**", "/api/user/**").permitAll()
                .antMatchers("/api/admin**", "/api/admin/**").permitAll()
                // TODO update antMatcher checks. Require service account authentication at all endpoints except root
//                .antMatchers("/api/authenticate").hasAnyRole("USER", "ADMIN")
                .antMatchers("/api**", "/api/**").permitAll()
                .and()
                .logout()
                .logoutRequestMatcher(AntPathRequestMatcher("/api/logout"))
                .deleteCookies("JSESSIONID")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .logoutSuccessUrl("/logout.done")
    }
}