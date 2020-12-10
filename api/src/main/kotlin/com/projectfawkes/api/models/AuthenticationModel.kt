package com.projectfawkes.api.models

// Deprecated. Don't use DatabaseURL anyways
//fun authenticate(username: String, password: String): User{
//    val restTemplate = RestTemplate()
//    val userJSON = restTemplate.getForEntity("${DATABASE_URL}getUserByUsername?username=${username}", String::class.java).body
//    val detailedUser: DetailedUser
//    try {
//        detailedUser = jacksonObjectMapper().readValue(userJSON ?: "")
//    } catch (e: UnrecognizedPropertyException) {
//        throw DataNotFoundException("Cannot authenticate. Data not found", e)
//    }
//    if (!BCrypt.checkpw(password, detailedUser.hash)) {
//        throw UnauthorizedException("Unauthenticated. Username or Password incorrect")
//    }
//    val userRecord = FirebaseAuth.getInstance().getUserByEmail(detailedUser.email)
//    return User(userRecord.uid, username, userRecord.email, userRecord.photoUrl)
//}