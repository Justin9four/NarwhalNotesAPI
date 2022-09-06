package com.projectfawkes.api.auth

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.config.annotation.CorsRegistry
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

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedMethods("GET", "PUT", "POST", "DELETE")
    }

    @Component
    class RequestMiddleware : HandlerInterceptor {

        private fun setHeaders(request: HttpServletRequest, response: HttpServletResponse) {
            response.setHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "x-auth-token")
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