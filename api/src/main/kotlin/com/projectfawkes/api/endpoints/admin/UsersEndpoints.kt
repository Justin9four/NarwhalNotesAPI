package com.projectfawkes.api.endpoints.admin

import com.projectfawkes.api.API_ENDPOINT
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

const val USERS_ENDPOINT = "$API_ENDPOINT/users"

@RestController
@RequestMapping(USERS_ENDPOINT)
class UsersEndpoints {
    @GetMapping
    fun getUsers(requestBody: HttpServletRequest): ResponseEntity<Any> {
        // get information about requested users
        // if no query params or ids provided all users retrieved
        return ResponseEntity("TODO finish get users Admin endpoint", HttpStatus.OK)
    }

    @PutMapping
    fun importUsers(requestBody: HttpServletRequest): ResponseEntity<Any> {
        // batch import. Can create other Admin accounts. Cannot create service account
        return ResponseEntity("TODO finish import users Admin endpoint", HttpStatus.OK)
    }

    @PostMapping("/enable-disable")
    fun disableAccounts(requestBody: HttpServletRequest): ResponseEntity<Any> {
        // can enable/disable Admin or User accounts
        return ResponseEntity("TODO finish enable-disable accounts endpoint", HttpStatus.OK)
    }

    @DeleteMapping("/purge")
    fun purgeAdmin(requestBody: HttpServletRequest): ResponseEntity<Any> {
        // purge admin accounts
        // must provide password to re-authenticate?
        return ResponseEntity("TODO finish purge users Admin endpoint", HttpStatus.OK)
    }

}