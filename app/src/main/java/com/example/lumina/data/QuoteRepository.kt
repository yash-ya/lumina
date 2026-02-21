package com.example.lumina.data

import android.content.Context
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString

class QuoteRepository(private val context: Context) {
    
    private val json = Json { ignoreUnknownKeys = true }

    fun loadQuotes(): List<Quote> {
        return try {
            val jsonString = context.assets.open("quotes.json").bufferedReader().use { it.readText() }
            json.decodeFromString<List<Quote>>(jsonString)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    fun getRandomQuote(): Quote? {
        val quotes = loadQuotes()
        return if (quotes.isNotEmpty()) quotes.random() else null
    }
}
