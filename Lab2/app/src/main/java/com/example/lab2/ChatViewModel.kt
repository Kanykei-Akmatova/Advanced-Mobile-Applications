package com.example.lab2

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getDatabase(application).messageDao()
    
    val messages: StateFlow<List<Message>> = dao.getAllMessages()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _editingMessage = MutableStateFlow<Message?>(null)
    val editingMessage: StateFlow<Message?> = _editingMessage.asStateFlow()

    fun onMessageSelected(message: Message) {
        _editingMessage.value = message
    }

    fun clearEditing() {
        _editingMessage.value = null
    }

    fun saveMessage(text: String, type: MessageType) {
        if (text.isBlank()) return
        val currentEditing = _editingMessage.value
        viewModelScope.launch {
            if (currentEditing != null) {
                dao.updateMessage(currentEditing.copy(text = text.trim()))
                _editingMessage.value = null
            } else {
                dao.insertMessage(Message(text = text.trim(), type = type))
            }
        }
    }

    fun deleteMessage(message: Message) {
        viewModelScope.launch {
            dao.deleteMessage(message)
        }
    }
}
