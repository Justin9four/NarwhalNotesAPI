package com.projectfawkes.api.controller.users

import com.projectfawkes.api.auth.AuthType
import com.projectfawkes.api.auth.UseAuth
import com.projectfawkes.api.controller.USERS_ENDPOINT
import com.projectfawkes.api.dataClass.Account
import com.projectfawkes.api.dataClass.Profile
import com.projectfawkes.api.dto.User
import com.projectfawkes.api.errorHandler.Field
import com.projectfawkes.api.errorHandler.Validator
import com.projectfawkes.api.service.deleteUser
import com.projectfawkes.api.service.getUser
import com.projectfawkes.api.service.updateUser
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest


@RestController
@RequestMapping(USERS_ENDPOINT)
@UseAuth(AuthType.USER)
class UserAccountsController {
    private val logger: Logger = LogManager.getLogger()

    @GetMapping
    fun getUser(request: HttpServletRequest): ResponseEntity<User> {
        val uid = SecurityContextHolder.getContext().authentication.principal.toString()
        return ResponseEntity(getUser(uid), HttpStatus.OK)
    }

    @PutMapping
    fun updateUser(request: HttpServletRequest, @RequestBody body: Map<String, String>): ResponseEntity<Any> {
        val uid = SecurityContextHolder.getContext().authentication.principal.toString()
        val fields =
            listOf(Field.FIRST_NAME, Field.LAST_NAME, Field.USERNAME, Field.DOB, Field.PASSWORD, Field.PHOTO_URL)
        val values = Validator(fields).validate(body, fields)

        val account = Account(uid, values[Field.USERNAME], null, values[Field.PHOTO_URL], null)
        val profile = Profile(
            uid, values[Field.FIRST_NAME], values[Field.LAST_NAME],
            null, values[Field.DOB]
        )
        updateUser(account, profile, values[Field.PASSWORD])
        return ResponseEntity(HttpStatus.OK)
    }

    @DeleteMapping
    fun deleteUser(requestBody: HttpServletRequest): ResponseEntity<Any> {
        val uid = SecurityContextHolder.getContext().authentication.principal.toString()
        deleteUser(uid)
        return ResponseEntity(HttpStatus.OK)
    }

}