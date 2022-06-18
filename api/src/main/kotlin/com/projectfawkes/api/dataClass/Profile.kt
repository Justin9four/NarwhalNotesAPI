package com.projectfawkes.api.dataClass

data class Profile(val uid: String?, val firstName: String?, val lastName: String?, val createdDate: String?, val dob: String?) {
    fun getProfileMap(): Map<String, String> {
        val profileMap = mutableMapOf<String, String>()
        if (!firstName.isNullOrBlank()) profileMap["firstName"] = firstName
        if (!lastName.isNullOrBlank()) profileMap["lastName"] = lastName
        if (!dob.isNullOrBlank()) profileMap["dob"] = dob
        if (!createdDate.isNullOrBlank()) profileMap["createdDate"] = createdDate
        return profileMap
    }
}