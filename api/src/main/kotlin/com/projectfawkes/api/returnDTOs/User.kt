package com.projectfawkes.api.returnDTOs

import com.projectfawkes.api.dataClasses.Account
import com.projectfawkes.api.dataClasses.Profile

data class User(var uid: String?, val username: String?, val email: String?, val photoUrl: String?,
                val firstName: String?, val lastName: String?, val createdDate: String?, val dob: String?) {
    constructor(account: Account, profile: Profile) :
            this(account.uid, account.username, account.email, account.photoUrl, profile.firstName,
                    profile.lastName, profile.createdDate, profile.dob)
}