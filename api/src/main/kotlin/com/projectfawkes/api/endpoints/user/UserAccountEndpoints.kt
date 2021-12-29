package com.projectfawkes.api.endpoints.user

import com.projectfawkes.api.USER_ENDPOINT
import com.projectfawkes.api.authentication.AuthType
import com.projectfawkes.api.authentication.UseAuth
import com.projectfawkes.api.dataClasses.Account
import com.projectfawkes.api.dataClasses.Profile
import com.projectfawkes.api.errorHandler.Field
import com.projectfawkes.api.errorHandler.Validator
import com.projectfawkes.api.models.deleteUser
import com.projectfawkes.api.models.getUser
import com.projectfawkes.api.models.updateUser
import com.projectfawkes.api.responseDTOs.User
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest


@RestController
@RequestMapping(USER_ENDPOINT)
@UseAuth(AuthType.USER)
class UserAccountEndpoints {
    private val logger: Logger = LogManager.getLogger()

    @GetMapping
    fun getUser(request: HttpServletRequest): ResponseEntity<User> {
        logger.info("Inside GET /api/user")
        val uid = request.getAttribute("uid").toString()
        return ResponseEntity(getUser(uid), HttpStatus.OK)
    }

    @PostMapping
    fun updateUser(request: HttpServletRequest, @RequestBody body: Map<String, String>): ResponseEntity<Any> {
        logger.info("Inside POST /api/user")
        val uid = request.getAttribute("uid").toString()
        val fields =
            listOf(Field.FIRST_NAME, Field.LAST_NAME, Field.USERNAME, Field.DOB, Field.PASSWORD, Field.PHOTO_URL)
        val values = Validator(fields).validate(body, fields)
        logger.info("update values: $values")

        val account = Account(uid, values[Field.USERNAME], null, values[Field.PHOTO_URL], null)
        val profile = Profile(
            uid, values[Field.FIRST_NAME], values[Field.LAST_NAME],
            null, values[Field.DOB]
        )
        updateUser(account, profile, values[Field.PASSWORD])
        return ResponseEntity(HttpStatus.OK)
    }

    @DeleteMapping
    fun deleteUser(requestBody: HttpServletRequest): ResponseEntity<Any> {
        logger.info("Inside DELETE /api/user")
        val uid = requestBody.getAttribute("uid").toString()
        deleteUser(uid)
        return ResponseEntity(HttpStatus.OK)
    }

}