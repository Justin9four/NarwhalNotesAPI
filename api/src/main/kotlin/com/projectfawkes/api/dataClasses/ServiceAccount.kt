package com.projectfawkes.api.dataClasses

data class ServiceAccount(val accountName: String,
                          val accountEmail: String,
                          val hash: String)