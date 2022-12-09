package com.projectfawkes.api.controller.admin

import com.projectfawkes.api.controller.ADMIN_ENABLE_DISABLE_ACCOUNT_ENDPOINT
import com.projectfawkes.api.controller.ADMIN_ENDPOINT
import com.projectfawkes.api.controller.ADMIN_PROMOTE_DEMOTE_ACCOUNT_ENDPOINT
import com.projectfawkes.api.controller.dto.EnableDisableAccountsDto
import com.projectfawkes.api.controller.dto.PromoteDemoteAccountDto
import com.projectfawkes.api.controller.dto.UserCompleteDto
import com.projectfawkes.api.dataClass.Account
import com.projectfawkes.api.dataClass.Profile
import com.projectfawkes.api.errorHandler.ValidationException
import com.projectfawkes.api.security.UserRoles
import com.projectfawkes.api.service.enableDisableAccount
import com.projectfawkes.api.service.getUsers
import com.projectfawkes.api.service.updateUser
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest
import javax.validation.Valid


@RestController
@RequestMapping(ADMIN_ENDPOINT)
class AdminController {
    @GetMapping
    fun getUsers(request: HttpServletRequest): ResponseEntity<List<UserCompleteDto>> {
        // get information about requested users
        // if no uid provided all users retrieved
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
    fun promoteDemoteAccount(
        @Valid @RequestBody promoteDemoteAccountDto: PromoteDemoteAccountDto,
        errors: BindingResult
    ): ResponseEntity<Any> {
        if (errors.hasErrors()) throw ValidationException(errors)
        // the way a normal user becomes an ADMIN
        val rolesList = mutableListOf(UserRoles.USER.value)
        if (promoteDemoteAccountDto.promoted.toBoolean()) rolesList.add(UserRoles.ADMIN.value)
        val account = Account(promoteDemoteAccountDto.uid, null, null, null, rolesList)
        val profile = Profile(promoteDemoteAccountDto.uid, null, null, null, null)
        updateUser(account, profile, null)
        return ResponseEntity(HttpStatus.OK)
    }

    @PutMapping(ADMIN_ENABLE_DISABLE_ACCOUNT_ENDPOINT)
    fun enableDisableAccounts(
        @Valid @RequestBody enableDisableAccountsDto: EnableDisableAccountsDto,
        errors: BindingResult
    ): ResponseEntity<Any> {
        if (errors.hasErrors()) throw ValidationException(errors)
        // can enable/disable Admin or User accounts
        val enabled: Boolean = enableDisableAccountsDto.enabled.toBoolean()
        enableDisableAccount(enableDisableAccountsDto.uid!!, enabled)
        return ResponseEntity(HttpStatus.OK)
    }
}