package com.projectfawkes.api.auth

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Configuration
class RequestHandler : WebMvcConfigurer {
    @Autowired
    val interceptor: RequestMiddleware? = null

    override fun addInterceptors(registry: InterceptorRegistry) {
        super.addInterceptors(registry)
        interceptor?.let { registry.addInterceptor(it) }
    }

    @Component
    class RequestMiddleware : HandlerInterceptor {
        @Value("\${host}")
        private val host: String? = null

        private val logger: Logger = LogManager.getLogger()

        private fun setHeaders(request: HttpServletRequest, response: HttpServletResponse) {
            response.setHeader("Access-Control-Allow-Origin", host)
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
            return true
        }
    }
}