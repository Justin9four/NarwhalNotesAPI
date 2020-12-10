package com.projectfawkes

import com.projectfawkes.responseObjects.Account
import com.projectfawkes.responseObjects.UpdateUser
import com.projectfawkes.responseObjects.User
import com.projectfawkes.utils.*
import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class UserEndpointsTest {
    private val user = User(null, "testUser123", "email@example.com", null, "firstName", "lastName", null, "12 25 1996")
    private var password = "testBabyYodaIsAwesome^2194ThisIsAPassword"

    private val authManager = AuthManager(user.username!!, password)


    @BeforeClass
    fun setUp() {
        testConnection()
    }

    @Test
    fun createUserSuccess() {
        val account = createUser(authManager, user.firstName!!, user.lastName!!, user.email!!, user.dob!!)
        user.uid = account.uid
    }

    @Test(dependsOnMethods = ["createUserSuccess"])
    fun getUserSuccess() {
        val user = getUser(authManager)
        assertEqualsUser(this.user, user)
    }

    @Test (dependsOnMethods = ["createUserSuccess", "getUserSuccess"])
    fun updateUserPhotoUrlByIdSuccess() {
        // TODO Investigate whether username can be changed. It might (probably will) require Spring Security changes
        //  if username is changed because it's something it keys off of
        this.user.photoUrl = "www.example.com"
        val userToUpdate = UpdateUser(photoUrl = this.user.photoUrl)

        val response = updateUser(authManager, userToUpdate)
        assertEquals(response.statusCode, HttpStatus.OK)
    }

    @Test(dependsOnMethods = ["createUserSuccess", "updateUserPhotoUrlByIdSuccess"], alwaysRun = true)
    fun authenticateAfterUpdateSuccess() {
        val account = authenticate(AuthManager(user.username!!, password))
        val expectedAccount = Account(user.uid, user.username, user.email, user.photoUrl, listOf("ROLE_USER"))
        assertEqualsAccount(expectedAccount, account)
    }

    @Test(dependsOnMethods = ["createUserSuccess", "authenticateAfterUpdateSuccess"])
    fun updateUserFirstNameAndLastNameByIdSuccess() {
        this.user.firstName = "Mark"
        this.user.lastName = "Hamill"
        val userToUpdate = UpdateUser(firstName = this.user.firstName, lastName = this.user.lastName)

        val response = updateUser(authManager, userToUpdate)
        assertEquals(response.statusCode, HttpStatus.OK)
    }

    @Test(dependsOnMethods = ["createUserSuccess", "updateUserFirstNameAndLastNameByIdSuccess"])
    fun getUserSuccess2() {
        getUserSuccess()
    }

    @Test(dependsOnMethods = ["createUserSuccess", "getUserSuccess2"], expectedExceptions = [HttpClientErrorException::class], alwaysRun = true)
    fun deleteUserSuccess() {
        try {
            val response = deleteUser(authManager)
            assertEquals(response.statusCode, HttpStatus.OK)
        } catch (e: HttpClientErrorException) {
            fail()
        }
        authenticate(authManager)
    }

    private fun assertEqualsUser(expectedUser: User, actualUser: User) {
        assertEquals(expectedUser.uid, actualUser.uid)
        assertEquals(expectedUser.username, actualUser.username)
        assertEquals(expectedUser.email, actualUser.email)
        assertEquals(expectedUser.photoUrl.toString(), actualUser.photoUrl)
        assertEquals(expectedUser.firstName, actualUser.firstName)
        assertEquals(expectedUser.lastName, actualUser.lastName)
        assertEquals(expectedUser.dob, actualUser.dob)
    }

    private fun assertEqualsAccount(expectedAccount: Account, actualAccount: Account) {
        assertEquals(expectedAccount.uid, actualAccount.uid)
        assertEquals(expectedAccount.username, actualAccount.username)
        assertEquals(expectedAccount.email, actualAccount.email)
        assertEquals(expectedAccount.photoUrl, actualAccount.photoUrl)
        assertEquals(expectedAccount.roles, actualAccount.roles)
    }
}
