package com.projectfawkes.api.service

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.UserRecord
import com.projectfawkes.api.controller.dto.AccountAndTokenDto
import com.projectfawkes.api.controller.dto.UserCompleteDto
import com.projectfawkes.api.controller.dto.UserDto
import com.projectfawkes.api.dataClass.Account
import com.projectfawkes.api.dataClass.Authentication
import com.projectfawkes.api.dataClass.Profile
import com.projectfawkes.api.errorHandler.DataConflictException
import com.projectfawkes.api.errorHandler.DataNotFoundException
import com.projectfawkes.api.errorHandler.UnauthorizedException
import com.projectfawkes.api.repository.AccountRepo
import com.projectfawkes.api.repository.AuthenticationRepo
import com.projectfawkes.api.repository.ProfileRepo
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.security.crypto.bcrypt.BCrypt
import java.util.*

private val logger: Logger = LogManager.getLogger()
private val profileRepo: ProfileRepo = ProfileRepo()
private val accountRepo: AccountRepo = AccountRepo()

fun register(account: Account, profile: Profile, password: String): AccountAndTokenDto {
    val uid = UUID.randomUUID().toString()
    account.uid = uid

    val passwordHash = BCrypt.hashpw(password, BCrypt.gensalt(10))!!
    val customToken = createAccount(account, passwordHash)
    profileRepo.create(uid, profile.getProfileMap())
    return AccountAndTokenDto(account, customToken)
}

fun authenticateCredentials(username: String, password: String): AccountAndTokenDto {
    return try {
        val authentication = AuthenticationRepo().get("username", username) as Authentication
        if (!BCrypt.checkpw(password, authentication.password)) {
            throw UnauthorizedException("Unauthenticated. Username or Password incorrect")
        }
        val customAuthToken = FirebaseAuth.getInstance().createCustomToken(authentication.account.uid)!!
        AccountAndTokenDto(authentication.account, customAuthToken)
    } catch (e: DataNotFoundException) {
        throw UnauthorizedException("Unauthenticated. Username or Password incorrect")
    }
}

fun getUser(uid: String): UserDto {
    val account = accountRepo.get("id", uid) as Account
    val profile = profileRepo.get("id", uid) as Profile
    return UserDto(account, profile)
}

fun getAccount(uid: String): Account {
    return accountRepo.get("id", uid) as Account
}

fun getUsers(uid: String?): List<UserCompleteDto> {
    val field = if (!uid.isNullOrBlank()) "id" else null
    val accounts = accountRepo.getValues(field, uid).filterIsInstance<Account>()
    val profiles = profileRepo.getValues(field, uid).filterIsInstance<Profile>()
    val userCompleteDtos = mutableListOf<UserCompleteDto>()
    for (account in accounts) {
        val profile = profiles.find { it.uid == account.uid }
        val enabled = !FirebaseAuth.getInstance().getUser(account.uid).isDisabled
        if (profile != null) {
            userCompleteDtos.add(UserCompleteDto(account, profile, enabled))
        }
    }
    return userCompleteDtos
}

fun enableDisableAccount(uid: String, enabled: Boolean) {
    val request = UserRecord.UpdateRequest(uid)
    request.setDisabled(!enabled)
    try {
        FirebaseAuth.getInstance().updateUser(request)
    } catch (e: FirebaseAuthException) {
        throw DataNotFoundException("User $uid Firebase error", e)
    }
}

fun getAccountByUsername(username: String): Account {
    return accountRepo.get("username", username) as Account
}

fun updateUser(account: Account, profile: Profile, password: String?) {
    var passwordHash = password
    if (!password.isNullOrBlank()) {
        passwordHash = BCrypt.hashpw(password, BCrypt.gensalt(10))!!
    }
    updateAccount(account, passwordHash)
    profileRepo.update(account.uid!!, profile.getProfileMap())
}

fun deleteUser(id: String) {
    profileRepo.delete(id)
    accountRepo.delete(id)
    FirebaseAuth.getInstance().deleteUser(id)
}

private fun createAccount(account: Account, passwordHash: String): String {
    accountRepo.create(account.uid!!, account.getAccountMap(passwordHash))
    val request = UserRecord.CreateRequest()
        .setEmail(account.email)
        .setEmailVerified(false)
        .setDisplayName(account.username)
        .setDisabled(false)
        .setUid(account.uid)
    val userRecord: UserRecord
    try {
        userRecord = FirebaseAuth.getInstance().createUser(request)
    } catch (e: FirebaseAuthException) {
        accountRepo.delete(account.uid!!)
        logger.error("Error creating user: " + e.message)
        throw DataConflictException("User ${account.uid} conflicts with Firebase users", e)
    }
    return FirebaseAuth.getInstance().createCustomToken(userRecord.uid)!!
}

private fun updateAccount(account: Account, password: String?) {
    accountRepo.update(account.uid!!, account.getAccountMap(password))
    val request = UserRecord.UpdateRequest(account.uid!!)
    if (account.username.isNullOrBlank()) {
        request.setDisplayName(account.username)
    }
    if (account.photoUrl.isNullOrBlank()) {
        request.setPhotoUrl(account.photoUrl)
    }
    try {
        FirebaseAuth.getInstance().updateUser(request)
    } catch (e: FirebaseAuthException) {
        accountRepo.delete(account.uid!!)
        logger.error("Error creating user: " + e.message)
        throw DataConflictException("User ${account.uid} conflicts with Firebase users", e)
    }
}