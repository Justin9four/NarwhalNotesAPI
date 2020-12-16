package com.projectfawkes.api.errorHandler

enum class Field(val value: String) {
    EMAIL("email"),
    PASSWORD("password"),
    FIRST_NAME("firstName"),
    LAST_NAME("lastName"),
    USERNAME("username"),
    DOB("dob"),
    TITLE("title"),
    TEXT("text"),
    ID("id"),
    PHOTO_URL("photoUrl")
}

data class ValidationError(val errorCode: Int, val details: ValidationErrorDetails) {
    fun checkIfActive(): Boolean {
        return details.fields.isNotEmpty()
    }
}

data class ValidationErrorDetails(val errorMessage: String, val fields: MutableList<Field> = mutableListOf())

class Validator(private val optionalFields: List<Field> = listOf()) {
    private val valueMustBeProvidedError = ValidationError(100, ValidationErrorDetails("A value must be provided"))

    private val values = mutableMapOf<Field, String>()

    fun validate(body: Map<String, String>, fields: List<Field>): Map<Field, String> {
        if (fields.isEmpty()) throw KotlinNullPointerException("Validation failed from empty validate")
        for (field in fields) {
            val value = body[field.value]
            values[field] = value ?: ""
            when (field) {
                Field.EMAIL -> validateEmail(value)
                Field.PASSWORD -> validatePassword(value)
                Field.FIRST_NAME -> validateFirstName(value)
                Field.LAST_NAME -> validateLastName(value)
                Field.USERNAME -> validateUsername(value)
                Field.DOB -> validateDOB(value)
                Field.TITLE -> ifFieldNullOrBlank(field, value)
                Field.TEXT -> ifFieldNullOrBlank(field, value)
                Field.ID -> ifFieldNullOrBlank(field, value)
                Field.PHOTO_URL -> ifFieldNullOrBlank(field, value)
            }
        }
        val validationErrors = createValidationErrors()
        if (validationErrors.isNotEmpty()) {
            throw ValidationException("Error validating input", validationErrors)
        }
        return values
    }

    private fun validateDOB(value: String?) {
        ifFieldNullOrBlank(Field.DOB, value)
    }

    private fun validateUsername(value: String?) {
        ifFieldNullOrBlank(Field.USERNAME, value)
    }

    private fun validateLastName(value: String?) {
        ifFieldNullOrBlank(Field.LAST_NAME, value)
    }

    private fun validateFirstName(value: String?) {
        ifFieldNullOrBlank(Field.FIRST_NAME, value)
    }

    private fun validatePassword(value: String?) {
        ifFieldNullOrBlank(Field.PASSWORD, value)
    }

    private fun validateEmail(value: String?) {
        ifFieldNullOrBlank(Field.EMAIL, value)
    }

    private fun ifFieldNullOrBlank(field: Field, value: String?) {
        if (value.isNullOrBlank()) {
            if (optionalFields.contains(field)) {
                // ignore this field
                values.remove(field)
                return
            }
            valueMustBeProvidedError.details.fields.add(field)
        }
    }

    private fun createValidationErrors(): MutableList<ValidationError> {
        val validationErrors: MutableList<ValidationError> = mutableListOf()
        if (valueMustBeProvidedError.checkIfActive()) {
            validationErrors.add(valueMustBeProvidedError)
        }
        return validationErrors
    }
}

