package com.projectfawkes.api.controller.admin

import com.projectfawkes.api.auth.AuthType
import com.projectfawkes.api.auth.Roles
import com.projectfawkes.api.auth.UseAuth
import com.projectfawkes.api.controller.ADMIN_ENABLE_DISABLE_ACCOUNT_ENDPOINT
import com.projectfawkes.api.controller.ADMIN_ENDPOINT
import com.projectfawkes.api.controller.ADMIN_PROMOTE_DEMOTE_ACCOUNT_ENDPOINT
import com.projectfawkes.api.dataClass.Account
import com.projectfawkes.api.dataClass.Profile
import com.projectfawkes.api.dto.UserComplete
import com.projectfawkes.api.errorHandler.Field
import com.projectfawkes.api.errorHandler.Validator
import com.projectfawkes.api.service.enableDisableAccount
import com.projectfawkes.api.service.getUsers
import com.projectfawkes.api.service.updateUser
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping(ADMIN_ENDPOINT)
@UseAuth(AuthType.ADMIN)
class AdminController {
    private val logger: Logger = LogManager.getLogger()

    @GetMapping
    fun getUsers(request: HttpServletRequest): ResponseEntity<List<UserComplete>> {
        logger.info("Inside GET /api/users/users")
        // get information about requested users
        // if no uid provided all users retrieved
        logger.info("Querying user with id: ${request.getParameter("uid")}")
        // add in additional query params
        val users = getUsers(request.getParameter("uid"))
        return ResponseEntity(users, HttpStatus.OK)
    }

    @PostMapping
    fun importUsers(request: HttpServletRequest): ResponseEntity<Any> {
        // batch import. Can create other Admin accounts. Cannot create service account
        return ResponseEntity("TODO finish import users Admin endpoint", HttpStatus.OK)
    }

    @PutMapping(ADMIN_PROMOTE_DEMOTE_ACCOUNT_ENDPOINT)
    fun promoteDemoteAccount(@RequestBody body: Map<String, String>): ResponseEntity<Any> {
        // the way a normal user becomes an ADMIN
        logger.info("Inside POST /api/users/promoteDemoteAccount")
        val values = Validator().validate(body, listOf(Field.UID, Field.PROMOTED))
        val rolesList = mutableListOf(Roles.USER.value)
        if (values[Field.PROMOTED]!!.toBoolean()) rolesList.add(Roles.ADMIN.value)
        val account = Account(values[Field.UID], null, null, null, rolesList)
        val profile = Profile(values[Field.UID], null, null, null, null)
        updateUser(account, profile, null)
        return ResponseEntity(HttpStatus.OK)
    }

    @PutMapping(ADMIN_ENABLE_DISABLE_ACCOUNT_ENDPOINT)
    fun enableDisableAccounts(@RequestBody body: Map<String, String>): ResponseEntity<Any> {
        // can enable/disable Admin or User accounts
        logger.info("Inside POST /api/users/enable-disable")
        val values = Validator().validate(body, listOf(Field.ENABLED, Field.UID))
        val enabled: Boolean = values[Field.ENABLED]!!.toBoolean()
        enableDisableAccount(values[Field.UID]!!, enabled)
        return ResponseEntity(HttpStatus.OK)
    }
}