package com.projectfawkes.api.authentication

import com.projectfawkes.api.data.ServiceAccountRepo
import com.projectfawkes.api.returnDTOs.ServiceAccount
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import java.security.Permissions

@Service
class ServiceAccountDetailsService : UserDetailsService {
    private val logger: Logger = LogManager.getLogger()

    override fun loadUserByUsername(username: String): UserDetails {
        return getAuthUser(username)
    }

    private fun getAuthUser(clientId: String): User {
        logger.info("Inside getAuthUser in ServiceAccountDetailsService")
        try {
            val serviceAccount = ServiceAccountRepo().get(ServiceAccount::clientId.name, clientId) as ServiceAccount
            val hashedPrivateKey = "{bcrypt}${serviceAccount.privateKey}"
            logger.info("hashed privateKey: $hashedPrivateKey")
            return User(clientId, hashedPrivateKey, listOf())
        } catch (e: Exception) {
            logger.error("Authentication error: $e")
            throw e
        }
    }
}

// ROLE prefix is required by Spring on roles
enum class Roles(val value: String) {
    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN")
}

class Authenticator {
    // take a uid and see if the session is still active and the user has the required permission for
    // the function
    fun isAuthorized(uid: String, requiredPermissions: Permissions): Boolean {
        return true
    }
}