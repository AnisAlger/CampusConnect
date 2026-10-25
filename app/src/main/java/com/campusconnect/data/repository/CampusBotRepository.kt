package com.campusconnect.data.repository

import com.campusconnect.data.model.MessageCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class CampusBotRepository {

    private val systemInstruction = """
        Tu es CampusBot, le tuteur académique et assistant d'études intelligent de l'application universitaire CampusConnect.
        Ton objectif est d'accompagner les étudiants dans leur réussite académique.
        Tes compétences principales sont :
        1. Expliquer des concepts de cours difficiles de manière simple, pédagogique, avec des métaphores parlantes et des exemples clairs.
        2. Générer des quiz stimulants (QCM avec solutions expliquées) et des flashcards (Question / Réponse) pour réviser efficacement.
        3. Donner des conseils concrets de méthodologie universitaire (méthode Feynman, technique Pomodoro, répétition espacée, préparation aux examens, gestion du stress).
        Adopte un ton bienveillant, motivant, professionnel et synthétique. Formate toujours tes réponses avec des puces et du gras pour une lecture rapide et agréable sur mobile.
    """.trimIndent()

    suspend fun generateResponse(
        userPrompt: String,
        category: MessageCategory,
        apiKey: String,
        modelName: String = "openai/gpt-3.5-turbo-16k"
    ): String = withContext(Dispatchers.IO) {
        val effectiveApiKey = apiKey.trim().ifEmpty { DataStoreManager.DEFAULT_OPENROUTER_KEY }

        try {
            val url = URL("https://openrouter.ai/api/v1/chat/completions")
            val connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                connectTimeout = 30000
                readTimeout = 45000
                doInput = true
                doOutput = true
                setRequestProperty("Content-Type", "application/json")
                setRequestProperty("Authorization", "Bearer $effectiveApiKey")
                setRequestProperty("HTTP-Referer", "https://campusconnect.edu")
                setRequestProperty("X-Title", "CampusConnect")
            }

            // Category guidance added to user prompt
            val categoryContext = when (category) {
                MessageCategory.EXPLANATION -> "[Instruction : Explique ce concept simplement avec des analogies adaptées à un étudiant] "
                MessageCategory.QUIZ -> "[Instruction : Génère un quiz interactif avec QCM ou flashcards et les réponses détaillées] "
                MessageCategory.METHODOLOGY -> "[Instruction : Donne des conseils méthodologiques concrets et applicables immédiatement] "
                else -> ""
            }

            val fullUserPrompt = categoryContext + userPrompt

            // Build JSON payload
            val rootJson = JSONObject().apply {
                put("model", modelName)

                val messagesArray = JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "system")
                        put("content", systemInstruction)
                    })
                    put(JSONObject().apply {
                        put("role", "user")
                        put("content", fullUserPrompt)
                    })
                }
                put("messages", messagesArray)
            }

            // Write request body
            OutputStreamWriter(connection.outputStream, "UTF-8").use { writer ->
                writer.write(rootJson.toString())
                writer.flush()
            }

            val responseCode = connection.responseCode
            if (responseCode in 200..299) {
                val responseString = BufferedReader(InputStreamReader(connection.inputStream, "UTF-8")).use { reader ->
                    reader.readText()
                }

                val responseJson = JSONObject(responseString)
                val choices = responseJson.optJSONArray("choices")
                if (choices != null && choices.length() > 0) {
                    val firstChoice = choices.getJSONObject(0)
                    val message = firstChoice.optJSONObject("message")
                    val content = message?.optString("content")
                    if (!content.isNullOrBlank()) {
                        return@withContext content.trim()
                    }
                }
                return@withContext "Réponse vide reçue de l'IA."
            } else {
                // Read error stream
                val errorString = try {
                    BufferedReader(InputStreamReader(connection.errorStream ?: connection.inputStream, "UTF-8")).use { reader ->
                        reader.readText()
                    }
                } catch (_: Exception) {
                    "Erreur HTTP $responseCode"
                }

                // If error, gracefully fallback
                return@withContext getSmartFallback(userPrompt, category, errorMsg = "HTTP $responseCode: $errorString")
            }
        } catch (e: Exception) {
            return@withContext getSmartFallback(userPrompt, category, errorMsg = e.localizedMessage)
        }
    }

    private fun getSmartFallback(prompt: String, category: MessageCategory, errorMsg: String?): String {
        val lowerPrompt = prompt.lowercase()

        return when {
            category == MessageCategory.EXPLANATION || lowerPrompt.contains("explique") || lowerPrompt.contains("concept") -> {
                """
                💡 **Explication Académique (CampusBot)**
                
                Voici comment aborder et retenir ce concept facilement :
                
                1. **L'analogie simple** : Imaginez ce mécanisme comme une chaîne de transmission : chaque maillon s'appuie sur le précédent pour produire le résultat final.
                2. **Le principe fondamental** : Décomposez l'idée en 3 piliers essentiels :
                   - *La cause / le point de départ* (l'entrée).
                   - *Le processus de transformation* (la règle ou loi appliquée).
                   - *Le résultat mesurable* (l'état final).
                3. **Application concrète** : Essayez d'expliquer ce principe avec vos propres mots à un camarade sans regarder vos notes (technique de Feynman).
                
                ${getNotice(errorMsg)}
                """.trimIndent()
            }
            category == MessageCategory.QUIZ || lowerPrompt.contains("quiz") || lowerPrompt.contains("flashcard") -> {
                """
                📝 **Quiz d'entraînement rapide (CampusBot)**
                
                **Question 1 :** Quelle est la clé d'une mémorisation à long terme efficace ?
                - A) Relire ses cours passivement plusieurs fois
                - B) La récupération active (Active Recall) et la répétition espacée
                - C) Réviser toute la nuit avant l'examen
                
                👉 **Réponse :** **B** ! Tester sa mémoire régulièrement est 3x plus efficace que la simple relecture.
                
                **Question 2 :** Dans la méthode Feynman, quelle est la dernière étape ?
                - A) Mémoriser par cœur les formules
                - B) Simplifier et utiliser des analogies compréhensibles par un enfant
                - C) Faire une pause café
                
                👉 **Réponse :** **B** ! Si vous ne pouvez pas l'expliquer simplement, c'est que vous ne l'avez pas encore totalement assimilé.
                
                ${getNotice(errorMsg)}
                """.trimIndent()
            }
            category == MessageCategory.METHODOLOGY || lowerPrompt.contains("méthode") || lowerPrompt.contains("réviser") || lowerPrompt.contains("pomodoro") -> {
                """
                ⏱️ **Guide Méthodologique & Révisions (CampusBot)**
                
                Voici 4 piliers prouvés pour maximiser vos notes à l'université :
                
                1. **La Technique Pomodoro 25/5** :
                   - 25 minutes de concentration absolue (zéro notification, zéro réseau social).
                   - 5 minutes de pause complète (étirez-vous, buvez de l'eau).
                   - Après 4 cycles : 30 minutes de grande pause.
                2. **La Règle des 80/20 (Principe de Pareto)** :
                   - 20% des concepts fondamentaux d'un cours représentent 80% des questions d'examen. Identifiez les notions récurrentes des annales !
                3. **Le Sommeil et la Consolidation** :
                   - C'est pendant la phase de sommeil profond que votre cerveau grave les informations de la journée dans la mémoire permanente.
                
                ${getNotice(errorMsg)}
                """.trimIndent()
            }
            else -> {
                """
                👋 **Bonjour ! Je suis CampusBot, votre tuteur académique OpenRouter.**
                
                Je suis là pour vous aider à réussir vos études :
                - 💡 **Expliquer un concept difficile** avec des mots simples.
                - 📝 **Générer un quiz ou des flashcards** pour tester vos connaissances.
                - ⏱️ **Optimiser vos méthodes de travail** et de révision.
                
                ${getNotice(errorMsg)}
                """.trimIndent()
            }
        }
    }

    private fun getNotice(errorMsg: String?): String {
        return if (errorMsg != null) {
            "\n\n*(Info connexion : $errorMsg)*"
        } else ""
    }
}
