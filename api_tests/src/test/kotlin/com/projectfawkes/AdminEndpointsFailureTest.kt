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
import kotlin.test.fail

class AdminEndpointsFailureTest {
    private val logger: Logger = LogManager.getLogger()

    private val user1 = User(
        null,
        "testUser987",
        "email987@example.com",
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
    fun getUsersUnauthorized() {
        try {
            getUsers(user1.username!!, account1Uid)
            fail()
        } catch (e: HttpClientErrorException) {
            if (e.statusCode != HttpStatus.UNAUTHORIZED) {
                fail("Expected Unauthorized exception")
            }
        }
    }

    @Test
    fun promoteAccountUnauthorized() {
        try {
            promoteDemoteAccount(user1.username!!, account1Uid, true)
            fail()
        } catch (e: HttpClientErrorException) {
            if (e.statusCode != HttpStatus.UNAUTHORIZED) {
                fail("Expected Unauthorized exception")
            }
        }
    }

    @Test
    fun disableAccountUnauthorized() {
        try {
            enableDisableAccount(user1.username!!, account1Uid, false)
            fail()
        } catch (e: HttpClientErrorException) {
            if (e.statusCode != HttpStatus.UNAUTHORIZED) {
                fail("Expected Unauthorized exception")
            }
        }
    }
}