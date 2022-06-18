package com.projectfawkes.api.dataClass

data class Note(val id: String?, val title: String?, val creator: String?, val createdDate: String?, val text: String?) {
    fun convertToMap(): Map<String, Any> {
        val noteMap = mutableMapOf<String, Any>()
        if (!title.isNullOrBlank()) noteMap["title"] = title
        if (!creator.isNullOrBlank()) noteMap["creator"] = creator
        if (!createdDate.isNullOrBlank()) noteMap["createdDate"] = createdDate
        if (!text.isNullOrBlank()) noteMap["text"] = text
        return noteMap
    }
}