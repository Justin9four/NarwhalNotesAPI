package com.projectfawkes.api.controller.dto

import javax.validation.constraints.NotEmpty

data class EnableDisableAccountsDto(
    @field:NotEmpty(message = "{uid.notNull}")
    val uid: String?,
    @field:NotEmpty(message = "{enabled.notNull}")
    val enabled: String?
)