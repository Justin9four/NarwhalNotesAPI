package com.projectfawkes.api

import com.google.cloud.firestore.Firestore
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.security.crypto.bcrypt.BCrypt

const val API_ENDPOINT = "/api"
const val USER_ENDPOINT = "$API_ENDPOINT/user"
var db: Firestore? = null
val BABY_YODA_HASH = BCrypt.hashpw("YK=,jqF^=,b\$Xc?hwbe2/Wk~~'~,?Q", BCrypt.gensalt(10))!!
const val SERVICE_ACCOUNT1_ID = "F68SDYGEHV79RG9W834CTY89WY7T8FCW84NHT7830WTHCF7HFT4F78ERHC78RGH748R7804TCH79MPSUGSY7459H9A"

@SpringBootApplication
class ApiApplication

fun main(args: Array<String>) {
    runApplication<ApiApplication>(*args) {
        firebaseInit()
    }
}
