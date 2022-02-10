package com.projectfawkes.api.authentication

import com.projectfawkes.api.models.getAccountByUsername
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest

@Component
@Profile("dev")
class AuthenticatorDev : AuthenticatorInterface {
    override val logger: Logger = LogManager.getLogger()
    override val registeredServiceAccountsEnv = "RegisteredServiceAccounts"
    override val host = "http://localhost:3000"

    override fun getUidFromSession(request: HttpServletRequest): String {
        val testUsername = request.getHeader("testUsername")
        logger.info("Test Username: $testUsername")
        if (testUsername.isNullOrBlank()) {
            return super.getUidFromSession(request)
        }
        if (testUsername.startsWith("test", ignoreCase = true)) {
            val account = getAccountByUsername(testUsername)
            return account.uid!!
        }
        return super.getUidFromSession(request)
    }
}