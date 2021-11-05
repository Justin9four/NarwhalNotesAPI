package com.projectfawkes.api

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.Firestore
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.cloud.FirestoreClient


const val FIREBASE_CREDENTIAL_ENV = "FirebaseCred"

fun firebaseInit() {
    val serviceAccountConfig = System.getenv(FIREBASE_CREDENTIAL_ENV).byteInputStream()
    val credentials = GoogleCredentials.fromStream(serviceAccountConfig)
    val builder = FirebaseOptions.builder()
    val options = builder.setCredentials(credentials).build()

    FirebaseApp.initializeApp(options)

    db = FirestoreClient.getFirestore()
}

fun getFirebaseDB(): Firestore? {
    return db
}