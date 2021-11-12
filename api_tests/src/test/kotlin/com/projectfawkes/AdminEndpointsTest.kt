package com.projectfawkes

import com.projectfawkes.responseObjects.User
import com.projectfawkes.utils.*
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException
import org.testng.annotations.AfterClass
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.fail

class AdminEndpointsTest {
    private val logger: Logger = LogManager.getLogger()

    private val user1 = User(
        null,
        "testUser123",
        "email1@example.com",
        null,
        "firstName",
        "lastName",
        null,
        "12 25 1996",
        listOf("ROLE_USER")
    )
    private var password = "testBabyYodaIsAwesome^2194ThisIsAPassword"
    private var account1Uid: String = ""

    @BeforeClass
    fun setUp() {
        testConnection()
        val account1 = createUser(
            user1.username!!, password, user1.firstName!!, user1.lastName!!,
            user1.email!!, user1.dob!!
        )
        account1Uid = account1.uid!!
        logger.info("Test user created: $account1Uid username= ${user1.username}")
    }

    @AfterClass
    fun tearDown() {
        enableDisableAccount("testMaster", account1Uid, true)
        deleteUser(user1.username!!)
        logger.info("Test user deleted: ${user1.username}")
    }

    @Test
    fun getUsersSuccess() {
        val users = getUsers("testMaster", null)
        print(users)
    }

    @Test(dependsOnMethods = ["enableAccountSuccess"])
    fun promoteAccountSuccess() {
        val response = promoteDemoteAccount("testMaster", account1Uid, true)
        assertEquals(response.statusCode, HttpStatus.OK)
        val users = getUsers(user1.username!!, account1Uid)
        print(users)
        assertEquals(users.size, 1)
        assertNotNull(users[0].roles!!.find { it == "ROLE_ADMIN" })
    }

    @Test
    fun disableAccountSuccess() {
        val response = enableDisableAccount("testMaster", account1Uid, false)
        assertEquals(response.statusCode, HttpStatus.OK)
        try {
            getUser(user1.username!!)
        } catch (e: HttpClientErrorException) {
            if (e.rawStatusCode == 401) {
                print(e.responseBodyAsString)
                assertEquals("Account disabled", e.responseBodyAsString)
                return
            }
        }
        fail()
    }

    @Test(dependsOnMethods = ["disableAccountSuccess"])
    fun enableAccountSuccess() {
        val response = enableDisableAccount("testMaster", account1Uid, true)
        assertEquals(response.statusCode, HttpStatus.OK)
        val user = getUser(user1.username!!)
        print(user)
    }

    @Test(dependsOnMethods = ["promoteAccountSuccess", "enableAccountSuccess"])
    fun demoteAccountSuccess() {
        val response = promoteDemoteAccount("testMaster", account1Uid, false)
        assertEquals(response.statusCode, HttpStatus.OK)
        val user = getUser(user1.username!!)
        print(user)
        assertNull(user.roles!!.find { it == "ROLE_ADMIN" })
    }
}
