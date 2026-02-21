package com.example.lumina.data

import android.content.Context
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class SavedQuotesRepository(context: Context) {
    private val prefs = context.getSharedPreferences("lumina_saved_quotes", Context.MODE_PRIVATE)
    private val json = Json { ignoreUnknownKeys = true }

    fun saveQuote(quote: Quote) {
        val currentSaved = loadSavedQuotes().toMutableList()
        if (currentSaved.none { it.id == quote.id }) {
            currentSaved.add(quote)
            val jsonString = json.encodeToString(currentSaved)
            prefs.edit().putString("saved_quotes", jsonString).apply()
        }
    }

    fun removeQuote(quoteId: Int) {
        val currentSaved = loadSavedQuotes().toMutableList()
        currentSaved.removeAll { it.id == quoteId }
        val jsonString = json.encodeToString(currentSaved)
        prefs.edit().putString("saved_quotes", jsonString).apply()
    }

    fun isQuoteSaved(quoteId: Int): Boolean {
        return loadSavedQuotes().any { it.id == quoteId }
    }

    fun loadSavedQuotes(): List<Quote> {
        val jsonString = prefs.getString("saved_quotes", null)
        return if (jsonString != null) {
            try {
                json.decodeFromString<List<Quote>>(jsonString)
            } catch (e: Exception) {
                emptyList()
            }
        } else {
            emptyList()
        }
    }
}
