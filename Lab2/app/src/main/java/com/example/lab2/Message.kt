package com.example.lab2

enum class MessageType {
    SEND, RECEIVE
}

data class Message(
    val id: Int,
    val text: String,
    val type: MessageType
)
