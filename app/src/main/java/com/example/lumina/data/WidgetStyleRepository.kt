package com.example.lumina.data

import android.content.Context
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class WidgetStyleRepository(context: Context) {
    private val prefs = context.getSharedPreferences("lumina_prefs", Context.MODE_PRIVATE)
    private val json = Json { ignoreUnknownKeys = true }

    fun saveStyle(style: WidgetStyle) {
        val jsonString = json.encodeToString(style)
        prefs.edit().putString("widget_style", jsonString).apply()
    }

    fun loadStyle(): WidgetStyle {
        val jsonString = prefs.getString("widget_style", null)
        return if (jsonString != null) {
            try {
                json.decodeFromString<WidgetStyle>(jsonString)
            } catch (e: Exception) {
                WidgetStyle()
            }
        } else {
            WidgetStyle()
        }
    }
}
