package com.projectfawkes.responseObjects

data class UpdateUser(val username: String? = null, val firstName: String? = null, val lastName: String? = null,
                      val email: String? = null, val dob: String? = null, val password: String? = null, val photoUrl: String? = null)