package com.projectfawkes.api.controller.dto

import javax.validation.constraints.NotEmpty

data class UpdateNoteDto(
    @field:NotEmpty(message = "{noteId.notNull}")
    val id: String?,
    val title: String?,
    val text: String?
)