package com.projectfawkes

import com.projectfawkes.responseObjects.User
import com.projectfawkes.utils.createUser
import com.projectfawkes.utils.deleteUser
import com.projectfawkes.utils.getUsers
import com.projectfawkes.utils.promoteAccount
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.http.HttpStatus
import org.testng.annotations.AfterClass
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test
import kotlin.test.assertEquals

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
    private val user2 = User(
        null,
        "testUser456",
        "email2@example.com",
        null,
        "firstName",
        "lastName",
        null,
        "12 25 1996",
        listOf("ROLE_USER")
    )
    private var password = "testBabyYodaIsAwesome^2194ThisIsAPassword"
    private var account1Uid: String = ""
    private var account2Uid: String = ""

    @BeforeClass
    fun setUp() {
        testConnection()
        val account1 = createUser(
            user1.username!!, password, user1.firstName!!, user1.lastName!!,
            user1.email!!, user1.dob!!
        )
        account1Uid = account1.uid!!
        logger.info("Test user created: $account1Uid username= ${user1.username}")
        val account2 = createUser(
            user2.username!!, password, user2.firstName!!, user2.lastName!!,
            user2.email!!, user2.dob!!
        )
        account2Uid = account2.uid!!
        logger.info("Test user created: $account2Uid username= ${user2.username}")
    }

    @AfterClass
    fun tearDown() {
        deleteUser(user1.username!!)
        logger.info("Test user deleted: ${user1.username}")
        deleteUser(user2.username!!)
        logger.info("Test user deleted: ${user2.username}")
    }

    @Test
    fun getUsersSuccess() {
        val users = getUsers("testAdmin")
        print(users)
    }

    @Test
    fun promoteAccountSuccess() {
        val response = promoteAccount("TODO put test admin username here", account1Uid)
        assertEquals(response.statusCode, HttpStatus.OK)
        val users = getUsers(user1.username!!)
        print(users)
    }
}
