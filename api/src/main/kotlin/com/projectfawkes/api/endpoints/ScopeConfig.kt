package com.projectfawkes.api.endpoints

import com.projectfawkes.api.dataClasses.Account
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope
import org.springframework.context.annotation.ScopedProxyMode
import org.springframework.web.context.WebApplicationContext

open class UserSession {
    var account: Account? = null
}

@Configuration
class ScopeConfig {

    @Bean
    @Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
    fun sessionScopedUser(): UserSession? {
        return UserSession()
    }

}
