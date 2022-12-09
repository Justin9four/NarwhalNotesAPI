package com.projectfawkes.api.controller.users

import com.projectfawkes.api.controller.USERS_ENDPOINT
import com.projectfawkes.api.controller.dto.UpdateUserDto
import com.projectfawkes.api.controller.dto.UserDto
import com.projectfawkes.api.dataClass.Account
import com.projectfawkes.api.dataClass.Profile
import com.projectfawkes.api.errorHandler.ValidationException
import com.projectfawkes.api.service.deleteUser
import com.projectfawkes.api.service.getUser
import com.projectfawkes.api.service.updateUser
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest
import javax.validation.Valid


@RestController
@RequestMapping(USERS_ENDPOINT)
class UserAccountsController {
    @GetMapping
    fun getUser(request: HttpServletRequest): ResponseEntity<UserDto> {
        val uid = SecurityContextHolder.getContext().authentication.principal.toString()
        return ResponseEntity(getUser(uid), HttpStatus.OK)
    }

    @PutMapping
    fun updateUser(
        request: HttpServletRequest,
        @Valid @RequestBody updateUserDto: UpdateUserDto,
        errors: BindingResult
    ): ResponseEntity<Any> {
        if (errors.hasErrors()) throw ValidationException(errors)
        val uid = SecurityContextHolder.getContext().authentication.principal.toString()

        val account = Account(uid, updateUserDto.username, null, updateUserDto.photoUrl, null)
        val profile = Profile(
            uid, updateUserDto.firstName, updateUserDto.lastName,
            null, updateUserDto.dob
        )
        updateUser(account, profile, updateUserDto.password)
        return ResponseEntity(HttpStatus.OK)
    }

    @DeleteMapping
    fun deleteUser(requestBody: HttpServletRequest): ResponseEntity<Any> {
        val uid = SecurityContextHolder.getContext().authentication.principal.toString()
        deleteUser(uid)
        return ResponseEntity(HttpStatus.OK)
    }

}