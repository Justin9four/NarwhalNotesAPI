package com.projectfawkes.api.data

import com.google.api.core.ApiFuture
import com.google.cloud.firestore.DocumentSnapshot
import com.google.cloud.firestore.QueryDocumentSnapshot
import com.projectfawkes.api.returnDTOs.ServiceAccount

class ServiceAccountRepo : RepoBaseClass("serviceAccounts") {
    override fun getReturnObject(document: QueryDocumentSnapshot): Any {
        return ServiceAccount(document.id, document.get(ServiceAccount::clientName.name).toString(), document.get(ServiceAccount::clientEmail.name).toString(), document.get(ServiceAccount::privateKey.name).toString())
    }

    override fun getReturnObject(future: ApiFuture<DocumentSnapshot>): Any {
        return ServiceAccount(future.get().id, future.get().get(ServiceAccount::clientName.name).toString(), future.get().get(ServiceAccount::clientEmail.name).toString(), future.get().get(ServiceAccount::privateKey.name).toString())
    }

    override fun getUniqueValuesCollection(): List<String> {
        return listOf(ServiceAccount::clientId.name, ServiceAccount::clientName.name, ServiceAccount::privateKey.name)
    }
}