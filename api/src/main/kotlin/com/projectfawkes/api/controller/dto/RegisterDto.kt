package com.projectfawkes.api.controller.dto

import javax.validation.constraints.NotEmpty

data class RegisterDto(
    @field:NotEmpty(message = "{username.notNull}")
    val username: String?,
    @field:NotEmpty(message = "{password.notNull}")
    val password: String?,
    @field:NotEmpty(message = "{email.notNull}")
    val email: String?,
    @field:NotEmpty(message = "{firstName.notNull}")
    val firstName: String?,
    @field:NotEmpty(message = "{lastName.notNull}")
    val lastName: String?,
    @field:NotEmpty(message = "{dob.notNull}")
    val dob: String?
)