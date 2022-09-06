package com.projectfawkes.api.controller.dto

import javax.validation.constraints.NotEmpty

data class PromoteDemoteAccountDto(
    @field:NotEmpty(message = "{uid.notNull}")
    val uid: String?,
    @field:NotEmpty(message = "{promoted.notNull}")
    val promoted: String?
)