package com.projectfawkes.api

import com.google.cloud.firestore.Firestore
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

const val API_ENDPOINT = "/api"
const val USER_ENDPOINT = "$API_ENDPOINT/user"
var db: Firestore? = null

@SpringBootApplication
class ApiApplication

fun main(args: Array<String>) {
    runApplication<ApiApplication>(*args) {
        firebaseInit()
    }
}
