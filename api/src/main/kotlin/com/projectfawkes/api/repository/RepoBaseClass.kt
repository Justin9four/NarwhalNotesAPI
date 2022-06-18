package com.projectfawkes.api.repository

import com.google.api.core.ApiFuture
import com.google.cloud.firestore.DocumentSnapshot
import com.google.cloud.firestore.QueryDocumentSnapshot
import com.google.cloud.firestore.WriteResult
import com.projectfawkes.api.errorHandler.DataConflictException
import com.projectfawkes.api.errorHandler.DataNotFoundException
import com.projectfawkes.api.getFirebaseDB
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

open class RepoBaseClass(private val collection: String) {
    private val logger: Logger = LogManager.getLogger()
    private val unsupportedOperationMessage = "Override this method to retrieve appropriate object"

    private fun verifyUniqueValues(data: Map<String, Any>) {
        // get() will throw an exception if more than one value is retrieved
        val valuesToVerify = getUniqueValuesCollection()
        val nonUniqueValues = mutableListOf<String>()
        for (value in valuesToVerify)
        {
            try {
                get(value, data[value].toString())
                nonUniqueValues.add(value)
            } catch (e: DataNotFoundException) {
                // DataNotFound means these values are unique
            }
        }
        if (nonUniqueValues.isNotEmpty()) {
            throw DataConflictException("$nonUniqueValues not unique")
        }
    }

    protected open fun getReturnObject(document: QueryDocumentSnapshot): Any {
        throw UnsupportedOperationException(unsupportedOperationMessage)
    }

    protected open fun getReturnObject(future: ApiFuture<DocumentSnapshot>): Any {
        throw UnsupportedOperationException(unsupportedOperationMessage)
    }

    protected open fun getUniqueValuesCollection() : List<String> {
        throw UnsupportedOperationException(unsupportedOperationMessage)
    }

    fun create(id: String, createData: Map<String, Any>): Any {
        if (createData.isEmpty()) throw KotlinNullPointerException("Create data cannot be empty")
        verifyUniqueValues(createData)
        val future: ApiFuture<WriteResult> = getFirebaseDB()!!.collection(collection).document(id).set(createData)
        future.get().updateTime // wait for create confirmation
        logger.info("Successfully created from $collection: $id")
        return get("id", id)
    }

    fun get(field: String, value: String): Any {
        val returnValues = getValues(field, value)
        if (returnValues.size > 1) {
            throw DataConflictException("Non-unique value: $value")
        }
        return returnValues[0]
    }

    fun getValues(field: String?, value: String?): MutableList<Any> {
        val returnValues = mutableListOf<Any>()
        when {
            field == "id" -> {
                val future = getFirebaseDB()!!.collection(collection).document(value!!).get()
                if (!future.get().exists()) throw DataNotFoundException("Data not found")
                returnValues.add(getReturnObject(future))
            }
            field != null -> {
                logger.info("getting data with field: $field and value: $value and collection: $collection")
                val future = getFirebaseDB()!!.collection(collection).whereEqualTo(field, value).get()
                val documents = future.get().documents
                if (documents.isEmpty()) throw DataNotFoundException("Cannot get $field")
                for (document in documents) {
                    returnValues.add(getReturnObject(document))
                }
            }
            else -> {
                logger.info("getting all documents in collection: $collection")
                val future = getFirebaseDB()!!.collection(collection).get()
                val documents = future.get().documents
                if (documents.isEmpty()) throw DataNotFoundException("No Documents Found")
                for (document in documents) {
                    returnValues.add(getReturnObject(document))
                }
            }
        }
        if (returnValues.isEmpty()) {
            throw DataNotFoundException("Cannot get object")
        }

        logger.info("Successfully retrieved from $collection: $returnValues")
        return returnValues
    }

    fun update(id: String, updateData: Map<String, Any>) {
        if (updateData.isEmpty()) return
        logger.info("Update Data $updateData")
        verifyUniqueValues(updateData)
        val future: ApiFuture<WriteResult> = getFirebaseDB()!!.collection(collection).document(id).update(updateData)
        future.get().updateTime // wait for update confirmation
        logger.info("Successfully updated from $collection: $id")
    }

    fun delete(id: String) {
        val future: ApiFuture<WriteResult> = getFirebaseDB()!!.collection(collection).document(id).delete()
        future.get().updateTime
        logger.info("Successfully deleted from $collection: $id")
    }
}