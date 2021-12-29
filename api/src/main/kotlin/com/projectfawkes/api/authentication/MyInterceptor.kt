package com.projectfawkes.api.authentication

import com.projectfawkes.api.errorHandler.UnauthorizedException
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.http.HttpHeaders
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class MyInterceptor : HandlerInterceptor {
    private val logger: Logger = LogManager.getLogger()
    private val authenticator = authenticatorFactory()

    private fun setHeaders(request: HttpServletRequest, response: HttpServletResponse) {
        // TODO Only set Access-Control-Allow-Origin like this for DEV not PROD
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000")
        response.setHeader("Access-Control-Allow-Credentials", "true")
        response.setHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "x-auth-token")
        if (request.method == "OPTIONS") {
            logger.info("OPTIONS ${request.requestURI}: Preflight Check")
            response.setHeader("Access-Control-Allow-Methods", "GET, PUT, POST, DELETE")
            response.setHeader("Access-Control-Allow-Headers", "Origin, Authorization, Content-Type, idToken")
        }
    }

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        // must return false because OPTIONS method will still enter other endpoint (PUT, POST, DELETE, etc)
        setHeaders(request, response)
        if (request.method == "OPTIONS") return false
        // cannot determine authType
        if (handler !is HandlerMethod) return false

        val authType =
            handler.getMethodAnnotation(UseAuth::class.java)?.authType
                ?: handler.method.declaringClass.getAnnotation(UseAuth::class.java)?.authType!!
        logger.info("${request.method} ${request.requestURI} ($authType)")
        when (authType) {
            AuthType.SERVICEACCOUNT -> authenticator.authenticateServiceAccount(request)
            AuthType.ADMIN -> {
                val roles = authenticator.authenticateSession(request).roles
                if (!roles!!.contains(Roles.ADMIN.value)) {
                    throw UnauthorizedException("Session Cookie Invalid")
                }
            }
            AuthType.USER -> authenticator.authenticateSession(request)
            AuthType.PUBLIC -> {}
        }
        return true
    }
}
