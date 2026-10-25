package com.campusconnect.data.model

import java.util.UUID

enum class MessageSender {
    USER, BOT
}

enum class MessageCategory {
    GENERAL, EXPLANATION, QUIZ, METHODOLOGY
}

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val sender: MessageSender,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val category: MessageCategory = MessageCategory.GENERAL,
    val isLoading: Boolean = false
)
