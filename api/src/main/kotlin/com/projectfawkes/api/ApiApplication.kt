package com.projectfawkes.api

import com.google.cloud.firestore.Firestore
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

var db: Firestore? = null

@SpringBootApplication
class ApiApplication

fun main(args: Array<String>) {
    runApplication<ApiApplication>(*args) {
        firebaseInit()
    }
}
