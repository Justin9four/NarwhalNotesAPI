package com.projectfawkes.api.service

// Copyright 2018 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

// Sourced and revised from https://github.com/gsuitedevs/java-samples/blob/master/drive/quickstart/src/main/java/DriveQuickstart.java

//import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp    THESE are the 2 dependencies with issues
//import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.drive.DriveScopes
import java.io.IOException


// TODO fix: there's a maven dependency conflict in this file... Application won't start
class GoogleDriveUtils {
    private val applicationsName = "Google Drive API Java Quickstart"
    private val jsonFactory: JsonFactory = JacksonFactory.getDefaultInstance()
    private val tokensDirectoryPath = "tokens"
    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private val scopes = listOf(DriveScopes.DRIVE_METADATA_READONLY)
    private val credentialsFilePath = "/firebaseCred.json"
    /**
     * Creates an authorized Credential object.
     * @param httpTransport The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    @Throws(IOException::class)
//    private fun getCredentials(httpTransport: NetHttpTransport): Credential { // Load client secrets.
//        val credentialResource = GoogleDriveUtils::class.java.getResourceAsStream(credentialsFilePath)
//                ?: throw FileNotFoundException("Resource not found: $credentialsFilePath")
//        val clientSecrets = GoogleClientSecrets.load(jsonFactory, InputStreamReader(credentialResource))
//        // Build flow and trigger user authorization request.
//        val flow = GoogleAuthorizationCodeFlow.Builder(
//                httpTransport, jsonFactory, clientSecrets, scopes)
//                .setDataStoreFactory(FileDataStoreFactory(java.io.File(tokensDirectoryPath)))
//                .setAccessType("offline")
//                .build()
//        val receiver: LocalServerReceiver = LocalServerReceiver.Builder().setPort(8888).build()
//        return AuthorizationCodeInstalledApp(flow, receiver).authorize("user")
//    }

    fun main(args: Array<String>) { // Build a new authorized API client service.
        val httpTransport = GoogleNetHttpTransport.newTrustedTransport()
//        val service: Drive = Drive.Builder(httpTransport, jsonFactory, getCredentials(httpTransport))
//                .setApplicationName(applicationsName)
//                .build()
        // Print the names and IDs for up to 10 files.
//        val result: FileList = service.files().list()
//                .setPageSize(10)
//                .setFields("nextPageToken, files(id, name)")
//                .execute()
//        val files: List<File> = result.files
//        if (files.isEmpty()) {
//            logger.info("No files found.")
//        } else {
//            logger.info("Files:")
//            for (file in files) {
//                System.out.printf("%s (%s)\n", file.name, file.id)
//            }
//        }
    }
}