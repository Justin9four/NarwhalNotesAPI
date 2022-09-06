package com.projectfawkes.api.controller.dto

import javax.validation.constraints.NotEmpty

data class AuthenticateDto(
    @field:NotEmpty(message = "{username.notNull}")
    val username: String?,
    @field:NotEmpty(message = "{password.notNull}")
    val password: String?
)