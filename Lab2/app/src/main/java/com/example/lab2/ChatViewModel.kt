package com.example.lab2

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ChatViewModel : ViewModel() {

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    private var nextId = 0

    fun sendMessage(text: String, type: MessageType) {
        if (text.isBlank()) return
        val newMessage = Message(
            id = nextId++,
            text = text.trim(),
            type = type
        )
        _messages.value = _messages.value + newMessage
    }
}
