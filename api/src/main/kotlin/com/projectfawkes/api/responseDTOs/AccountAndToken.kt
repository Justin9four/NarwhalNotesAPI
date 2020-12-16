package com.projectfawkes.api.responseDTOs

import com.projectfawkes.api.dataClasses.Account

data class AccountAndToken(val account: Account, val token: String)