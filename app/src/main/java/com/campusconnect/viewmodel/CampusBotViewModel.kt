package com.campusconnect.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.campusconnect.data.model.ChatMessage
import com.campusconnect.data.model.MessageCategory
import com.campusconnect.data.model.MessageSender
import com.campusconnect.data.repository.CampusBotRepository
import com.campusconnect.data.repository.DataStoreManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CampusBotViewModel(
    private val repository: CampusBotRepository = CampusBotRepository(),
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _currentCategory = MutableStateFlow(MessageCategory.GENERAL)
    val currentCategory: StateFlow<MessageCategory> = _currentCategory.asStateFlow()

    val apiKey: StateFlow<String> = MutableStateFlow(DataStoreManager.DEFAULT_OPENROUTER_KEY)
    val selectedModel: StateFlow<String> = MutableStateFlow(DataStoreManager.DEFAULT_MODEL)

    init {
        // Observe saved API key and model
        viewModelScope.launch {
            dataStoreManager.openRouterApiKey.collect { key ->
                (apiKey as MutableStateFlow).value = key.ifBlank { DataStoreManager.DEFAULT_OPENROUTER_KEY }
            }
        }

        viewModelScope.launch {
            dataStoreManager.openRouterModel.collect { model ->
                (selectedModel as MutableStateFlow).value = model.ifBlank { DataStoreManager.DEFAULT_MODEL }
            }
        }

        // Add welcome message
        _messages.value = listOf(
            ChatMessage(
                sender = MessageSender.BOT,
                content = "👋 **Bonjour ! Je suis CampusBot, propulsé par OpenRouter.**\n\nJe suis votre tuteur académique interactif. Comment puis-je vous aider ?\n\n• 💡 **Expliquer un concept difficile** avec des analogies simples\n• 📝 **Générer des quiz ou flashcards** pour tester vos connaissances\n• ⏱️ **Conseils de méthodologie de travail universitaire**\n\nPosez votre question ou appuyez sur une suggestion ci-dessous !",
                category = MessageCategory.GENERAL
            )
        )
    }

    fun setCategory(category: MessageCategory) {
        _currentCategory.value = category
    }

    fun sendMessage(userPrompt: String, category: MessageCategory = _currentCategory.value) {
        if (userPrompt.isBlank() || _isGenerating.value) return

        val userMessage = ChatMessage(
            sender = MessageSender.USER,
            content = userPrompt.trim(),
            category = category
        )

        val loadingBotMessage = ChatMessage(
            sender = MessageSender.BOT,
            content = "",
            category = category,
            isLoading = true
        )

        _messages.value = _messages.value + userMessage + loadingBotMessage
        _isGenerating.value = true

        viewModelScope.launch {
            try {
                val key = apiKey.value
                val model = selectedModel.value
                val responseText = repository.generateResponse(userPrompt, category, key, model)

                // Replace loading message with real response
                _messages.value = _messages.value.filter { !it.isLoading } + ChatMessage(
                    sender = MessageSender.BOT,
                    content = responseText,
                    category = category
                )
            } catch (e: Exception) {
                _messages.value = _messages.value.filter { !it.isLoading } + ChatMessage(
                    sender = MessageSender.BOT,
                    content = "Désolé, une erreur est survenue : ${e.localizedMessage}",
                    category = category
                )
            } finally {
                _isGenerating.value = false
            }
        }
    }

    fun saveApiKey(newKey: String) {
        viewModelScope.launch {
            dataStoreManager.setOpenRouterApiKey(newKey)
        }
    }

    fun saveModel(model: String) {
        viewModelScope.launch {
            dataStoreManager.setOpenRouterModel(model)
        }
    }

    fun clearChat() {
        _messages.value = listOf(
            ChatMessage(
                sender = MessageSender.BOT,
                content = "La conversation a été réinitialisée. Quel sujet souhaitez-vous explorer ?",
                category = MessageCategory.GENERAL
            )
        )
    }
}
