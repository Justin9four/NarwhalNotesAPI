package com.projectfawkes.api.controller.dto

data class UpdateUserDto(
    val username: String?,
    val password: String?,
    val firstName: String?,
    val lastName: String?,
    val dob: String?,
    val photoUrl: String?
)