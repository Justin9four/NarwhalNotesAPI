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

    @BeforeClass
    fun setUp() {
        testConnection()
    }

    @Test
    fun createUserSuccess() {
        val account = createUser(user.username!!, password, user.firstName!!, user.lastName!!, user.email!!, user.dob!!)
        user.uid = account.uid
    }

    @Test(dependsOnMethods = ["createUserSuccess"])
    fun getUserSuccess() {
        val user = getUser(user.username!!)
        assertEqualsUser(this.user, user)
    }

    @Test(dependsOnMethods = ["createUserSuccess", "getUserSuccess"])
    fun updateUserPhotoUrlByIdSuccess() {
        this.user.photoUrl = "www.example.com"
        val userToUpdate = UpdateUser(photoUrl = this.user.photoUrl)

        val response = updateUser(user.username!!, userToUpdate)
        assertEquals(response.statusCode, HttpStatus.OK)
    }

    @Test(dependsOnMethods = ["createUserSuccess", "getUserSuccess"])
    fun updatePasswordSuccess() {
        password += "updated21"
        val userToUpdate = UpdateUser(password = password)

        val response = updateUser(user.username!!, userToUpdate)
        assertEquals(response.statusCode, HttpStatus.OK)
    }

    @Test(dependsOnMethods = ["createUserSuccess", "updateUserPhotoUrlByIdSuccess"], alwaysRun = true)
    fun authenticateAfterUpdateSuccess() {
        val account = authenticate(user.username!!, password)
        val expectedAccount = Account(user.uid, user.username, user.email, user.photoUrl, listOf("ROLE_USER"))
        assertEqualsAccount(expectedAccount, account)
    }

    @Test(dependsOnMethods = ["createUserSuccess", "authenticateAfterUpdateSuccess"])
    fun updateUserFirstNameAndLastNameByIdSuccess() {
        this.user.firstName = "Mark"
        this.user.lastName = "Hamill"
        val userToUpdate = UpdateUser(firstName = this.user.firstName, lastName = this.user.lastName)

        val response = updateUser(user.username!!, userToUpdate)
        assertEquals(response.statusCode, HttpStatus.OK)
    }

    @Test(dependsOnMethods = ["createUserSuccess", "updateUserFirstNameAndLastNameByIdSuccess"])
    fun getUserSuccess2() {
        getUserSuccess()
    }

    @Test(dependsOnMethods = ["createUserSuccess", "getUserSuccess2"], expectedExceptions = [HttpClientErrorException::class], alwaysRun = true)
    fun deleteUserSuccess() {
        try {
            val response = deleteUser(user.username!!)
            assertEquals(response.statusCode, HttpStatus.OK)
        } catch (e: HttpClientErrorException) {
            fail()
        }
        authenticate(user.username!!, password)
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
