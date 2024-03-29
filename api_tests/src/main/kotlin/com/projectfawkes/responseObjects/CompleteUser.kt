package com.projectfawkes.responseObjects

data class CompleteUser(
    var uid: String?, var username: String?, var email: String?, var photoUrl: String?,
    var firstName: String?, var lastName: String?, var createdDate: String?, var dob: String?,
    var roles: List<String>?, val enabled: Boolean
)