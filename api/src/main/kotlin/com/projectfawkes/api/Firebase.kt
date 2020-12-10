package com.projectfawkes.api

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.Firestore
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.cloud.FirestoreClient
import java.io.*
import java.util.stream.Collectors
import java.io.ByteArrayInputStream

import java.io.InputStream





const val FIREBASE_CREDENTIAL_ENV = "FirebaseCred"

fun firebaseInit() {
    val serviceAccountConfig = System.getenv(FIREBASE_CREDENTIAL_ENV).byteInputStream()
    val credentials = GoogleCredentials.fromStream(serviceAccountConfig)
    val options = FirebaseOptions.Builder()
            .setCredentials(credentials)
            .build()

    FirebaseApp.initializeApp(options)

    db = FirestoreClient.getFirestore()
}

fun getFirebaseDB(): Firestore? {
    return db
}