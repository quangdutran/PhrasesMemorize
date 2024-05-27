package com.dutq.mywordingapp.gemini

import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.future.future
import kotlinx.coroutines.withContext
import java.util.concurrent.CompletableFuture

class Gemini {
    private fun buildPrompt(phrase: String): String {
        return "Make a sentence, nothing more, in German level B1 using this phrase \"$phrase\", use either you or he or she or you guys as subject."
    }
    private suspend fun getPromptResponse(phrase: String): String? {
        val generativeModel = GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = com.dutq.mywordingapp.BuildConfig.API_KEY
        )

        val prompt = buildPrompt(phrase)
        val response = generativeModel.generateContent(prompt)
        return response.text
    }

    suspend fun sendPrompt(phrase: String): String? {
        return withContext(Dispatchers.IO) {
            getPromptResponse(phrase)
        }
    }
    fun sendPromptAsync(phrase: String): CompletableFuture<String?> = GlobalScope.future { sendPrompt(phrase) }
}