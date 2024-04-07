package com.demo.chat

data class ChatDetail(
    val message: String = "",
    val userId : String = "",
    val timestamp: Long = 0,
    val userName : String = "",
   // val userImageUrl: String
)

data class Message(
    val text: String = "",
    val username: String = "",
    val timestamp: Long = 0,
    val userId : String = "",
    )




