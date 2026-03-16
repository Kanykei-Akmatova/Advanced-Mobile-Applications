package com.example.lab2

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class MessageType {
    SEND, RECEIVE
}

@Entity(tableName = "messages")
data class Message(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val text: String,
    val type: MessageType
)
