package com.projectfawkes.api.controller.dto

import com.projectfawkes.api.dataClass.Account

data class AccountAndTokenDto(val account: Account, val token: String)