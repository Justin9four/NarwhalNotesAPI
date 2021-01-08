package com.projectfawkes.api.models

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.UserRecord
import com.projectfawkes.api.data.AccountRepo
import com.projectfawkes.api.data.AuthenticationRepo
import com.projectfawkes.api.data.ProfileRepo
import com.projectfawkes.api.dataClasses.Account
import com.projectfawkes.api.dataClasses.Authentication
import com.projectfawkes.api.dataClasses.Profile
import com.projectfawkes.api.errorHandler.DataConflictException
import com.projectfawkes.api.errorHandler.DataNotFoundException
import com.projectfawkes.api.errorHandler.UnauthorizedException
import com.projectfawkes.api.responseDTOs.AccountAndToken
import com.projectfawkes.api.responseDTOs.User
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.security.crypto.bcrypt.BCrypt
import java.util.*

private val logger: Logger = LogManager.getLogger()
private val profileRepo: ProfileRepo = ProfileRepo()
private val accountRepo: AccountRepo = AccountRepo()

fun register(account: Account, profile: Profile, password: String): AccountAndToken {
    val uid = UUID.randomUUID().toString()
    account.uid = uid

    val passwordHash = BCrypt.hashpw(password, BCrypt.gensalt(10))!!
    val customToken = createAccount(account, passwordHash)
    profileRepo.create(uid, profile.getProfileMap())
    return AccountAndToken(account, customToken)
}

fun getAccountAndToken(field: String, value: String): AccountAndToken {
    val account = accountRepo.get(field, value) as Account
    val customAuthToken = FirebaseAuth.getInstance().createCustomToken(account.uid)!!
    return AccountAndToken(account, customAuthToken)
}

fun authenticateCredentials(username: String, password: String): AccountAndToken
{
    return try {
        val authentication = AuthenticationRepo().get("username", username) as Authentication
        if (!BCrypt.checkpw(password, authentication.password)) {
            throw UnauthorizedException("Unauthenticated. Username or Password incorrect")
        }
        val customAuthToken = FirebaseAuth.getInstance().createCustomToken(authentication.account.uid)!!
        AccountAndToken(authentication.account, customAuthToken)
    } catch (e: DataNotFoundException) {
        throw UnauthorizedException("Unauthenticated. Username or Password incorrect")
    }
}

fun authenticateToken(token: String): AccountAndToken
{
    return try {
        val decodedToken = FirebaseAuth.getInstance().verifyIdToken(token)
        val uid = decodedToken.uid
        val authentication = AuthenticationRepo().get("id", uid) as Authentication
        AccountAndToken(authentication.account, token)
    } catch (e: FirebaseAuthException) {
        throw UnauthorizedException("Token unauthorized. Login failed")
    }
}

fun getUser(uid: String): User {
    val account = accountRepo.get("id", uid) as Account
    val profile = profileRepo.get("id", uid) as Profile
    return User(account, profile)
}

fun getAccount(uid: String): Account {
    return accountRepo.get("id", uid) as Account
}

fun getUsers(): List<User> {
    val accounts = accountRepo.getValues(null, null).filterIsInstance<Account>()
    val profiles = profileRepo.getValues(null, null).filterIsInstance<Profile>()
    val users = mutableListOf<User>()
    for (account in accounts) {
        val profile = profiles.find { it.uid == account.uid }
        if (profile != null) {
            users.add(User(account, profile))
        }
    }
    return users
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
    logger.info("Successfully updated user: " + account.uid)
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
    logger.info("Successfully created user: " + userRecord.uid)
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