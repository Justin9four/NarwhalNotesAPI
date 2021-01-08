package com.projectfawkes.api.responseDTOs

import com.projectfawkes.api.dataClasses.Account
import com.projectfawkes.api.dataClasses.Profile

data class User(
    var uid: String?, val username: String?, val email: String?, val photoUrl: String?,
    val firstName: String?, val lastName: String?, val createdDate: String?, val dob: String?,
    val roles: List<String>?
) {
    constructor(account: Account, profile: Profile) :
            this(
                account.uid, account.username, account.email, account.photoUrl, profile.firstName,
                profile.lastName, profile.createdDate, profile.dob, account.roles
            )
}