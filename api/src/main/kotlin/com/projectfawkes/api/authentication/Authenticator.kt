package com.projectfawkes.api.authentication
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("production")
class Authenticator : AuthenticatorInterface {
    override val logger: Logger = LogManager.getLogger()
    override val registeredServiceAccountsEnv = "RegisteredServiceAccounts"
}