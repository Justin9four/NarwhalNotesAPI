package com.projectfawkes.api.controller

// API
const val API_ENDPOINT = "/api"

// ADMIN
const val ADMIN_ENDPOINT = "$API_ENDPOINT/admin/users"
const val ADMIN_PROMOTE_DEMOTE_ACCOUNT_ENDPOINT = "/promote-demote"
const val ADMIN_ENABLE_DISABLE_ACCOUNT_ENDPOINT = "/enable-disable"

// USERS
const val USERS_ENDPOINT = "$API_ENDPOINT/users"

// NOTES
const val NOTES_ENDPOINT = "$USERS_ENDPOINT/notes"
const val NOTES_BY_ID_ENDPOINT = "/{id}"
const val NOTES_UPLOAD_GOOGLE_DRIVE_ENDPOINT = "/uploadGoogleDrive"
const val NOTES_SYNC_PUSHER_ENDPOINT = "/pusher"

// AUTH
const val AUTHENTICATE_ENDPOINT = "/authenticate"
const val REGISTER_ENDPOINT = "/register"
const val CHECK_TOKEN_ENDPOINT = "/checkToken"
const val SIGN_OUT_ENDPOINT = "/signOut"