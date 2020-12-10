package com.projectfawkes.api.returnDTOs

import com.projectfawkes.api.dataClasses.Account

data class AccountAndToken(val account: Account, val token: String)