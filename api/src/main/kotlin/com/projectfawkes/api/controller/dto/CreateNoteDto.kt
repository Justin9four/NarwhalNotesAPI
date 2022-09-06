package com.projectfawkes.api.controller.dto

import javax.validation.constraints.NotEmpty

data class CreateNoteDto(@field:NotEmpty(message = "{noteTitle.notNull}") val title: String?, val text: String?)