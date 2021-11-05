package com.projectfawkes.api.endpoints.admin

import com.projectfawkes.api.API_ENDPOINT
import com.projectfawkes.api.authentication.Roles
import com.projectfawkes.api.dataClasses.Account
import com.projectfawkes.api.dataClasses.Profile
import com.projectfawkes.api.models.getUsers
import com.projectfawkes.api.models.updateUser
import com.projectfawkes.api.responseDTOs.User
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

const val USERS_ENDPOINT = "$API_ENDPOINT/users"
const val PROMOTE_ACCOUNT_ENDPOINT = "/promoteAccount"
const val DEMOTE_ACCOUNT_ENDPOINT = "/demoteAccount"
const val ENABLE_DISABLE_ENDPOINT = "/enable-disable"

@RestController
@RequestMapping(USERS_ENDPOINT)
class UsersEndpoints {
    private val logger: Logger = LogManager.getLogger()

    @GetMapping
    fun getUsers(request: HttpServletRequest): ResponseEntity<List<User>> {
        logger.info("Inside POST /api/users/promoteAccount")
        // get information about requested users
        // if no uid provided all users retrieved
        logger.info("Querying user with id: ${request.getParameter("uid")}")
        // add in additional query params
        val users = getUsers(request.getParameter("uid"))
        return ResponseEntity(users, HttpStatus.OK)
    }

    @PutMapping
    fun importUsers(request: HttpServletRequest): ResponseEntity<Any> {
        // batch import. Can create other Admin accounts. Cannot create service account
        return ResponseEntity("TODO finish import users Admin endpoint", HttpStatus.OK)
    }

    @PostMapping(PROMOTE_ACCOUNT_ENDPOINT)
    fun promoteAccount(@RequestBody body: Map<String, String>): ResponseEntity<Any> {
        // the way a normal user becomes an ADMIN
        logger.info("Inside POST /api/users/promoteAccount")
        val account = Account(body["uid"], null, null, null, listOf(Roles.ADMIN.value, Roles.USER.value))
        val profile = Profile(body["uid"], null, null, null, null)
        updateUser(account, profile, null)
        return ResponseEntity(HttpStatus.OK)
    }

    @PostMapping(DEMOTE_ACCOUNT_ENDPOINT)
    fun demoteAccount(@RequestBody body: Map<String, String>): ResponseEntity<Any> {
        // the way a normal user becomes an ADMIN
        val account = Account(body["uid"], null, null, null, listOf(Roles.USER.value))
        val profile = Profile(body["uid"], null, null, null, null)
        updateUser(account, profile, null)
        return ResponseEntity(HttpStatus.OK)
    }

    @PostMapping(ENABLE_DISABLE_ENDPOINT)
    fun disableAccounts(request: HttpServletRequest): ResponseEntity<Any> {
        // can enable/disable Admin or User accounts
        return ResponseEntity("TODO finish enable-disable accounts endpoint", HttpStatus.OK)
    }

}