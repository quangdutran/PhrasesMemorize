package com.dutq.mywordingapp.gemini

import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.future.future
import kotlinx.coroutines.withContext
import java.util.concurrent.CompletableFuture

class Gemini {
    suspend fun getPromptResponse(): String? {
        val generativeModel = GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = com.dutq.mywordingapp.BuildConfig.API_KEY
        )

        val prompt = "Write a story about a magic backpack."
        val response = generativeModel.generateContent(prompt)
        return response.text
    }

    suspend fun sendPrompt(): String? {
        return withContext(Dispatchers.IO) {
            getPromptResponse()
        }
    }
    fun sendPromptAsync(): CompletableFuture<String?> = GlobalScope.future { sendPrompt() }
}