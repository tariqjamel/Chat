package com.demo.chat

class ContactDetail {
    var name: String? = null
    var number: String? = null
    var imageResource: Int = 0
}

class Constants {
    companion object {
        const val BASE_URL = "firebase url"
        const val SERVER_KEY = "Your firebase server key"
        const val CONTENT_TYPE = "application/json"
    }
}

data class PushNotification(
    val data: Message,
    val to: String
)

