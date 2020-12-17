package com.projectfawkes.api.endpoints.admin

import com.projectfawkes.api.API_ENDPOINT
import com.projectfawkes.api.authentication.Roles
import com.projectfawkes.api.dataClasses.Account
import com.projectfawkes.api.dataClasses.Profile
import com.projectfawkes.api.models.getUsers
import com.projectfawkes.api.models.updateUser
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

const val USERS_ENDPOINT = "$API_ENDPOINT/users"
const val PROMOTE_ACCOUNT_ENDPOINT = "/promoteAccount"
const val ENABLE_DISABLE_ENDPOINT = "/enable-disable"
const val PURGE_ADMIN_ENDPOINT = "/purge"

@RestController
@RequestMapping(USERS_ENDPOINT)
class UsersEndpoints {
    @GetMapping
    fun getUsers(request: HttpServletRequest): ResponseEntity<Any> {
        // get information about requested users
        // if no query params or ids provided all users retrieved
        val users = getUsers()
        return ResponseEntity(users, HttpStatus.OK)
    }

    @PutMapping
    fun importUsers(request: HttpServletRequest): ResponseEntity<Any> {
        // batch import. Can create other Admin accounts. Cannot create service account
        return ResponseEntity("TODO finish import users Admin endpoint", HttpStatus.OK)
    }

    @PostMapping(PROMOTE_ACCOUNT_ENDPOINT)
    fun promoteAccount(@RequestBody uid: String): ResponseEntity<Any> {
        // the way a normal user becomes an ADMIN
        val account = Account(uid, null, null, null, listOf(Roles.ADMIN.value))
        val profile = Profile(uid, null, null, null, null)
        updateUser(account, profile, null)
        return ResponseEntity("TODO finish promote account Admin endpoint", HttpStatus.OK)
    }

    @PostMapping(ENABLE_DISABLE_ENDPOINT)
    fun disableAccounts(request: HttpServletRequest): ResponseEntity<Any> {
        // can enable/disable Admin or User accounts
        return ResponseEntity("TODO finish enable-disable accounts endpoint", HttpStatus.OK)
    }

    @DeleteMapping(PURGE_ADMIN_ENDPOINT)
    fun purgeAdmin(request: HttpServletRequest): ResponseEntity<Any> {
        // purge admin accounts
        // must provide password to re-authenticate?
        return ResponseEntity("TODO finish purge users Admin endpoint", HttpStatus.OK)
    }

}