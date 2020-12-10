package com.projectfawkes.responseObjects

data class UpdateNote(val id: String, val title: String? = null, val creator: String? = null, val text: String? = null)