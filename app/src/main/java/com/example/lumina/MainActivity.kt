package com.example.lumina

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.lifecycle.lifecycleScope
import androidx.glance.appwidget.updateAll
import com.example.lumina.data.QuoteRepository
import com.example.lumina.data.SavedQuotesRepository
import com.example.lumina.data.WidgetStyleRepository
import com.example.lumina.ui.HomeScreen
import com.example.lumina.ui.PreferencesScreen
import com.example.lumina.ui.SavedQuotesScreen
import com.example.lumina.ui.WidgetStudioScreen
import com.example.lumina.ui.theme.LuminaTheme
import com.example.lumina.widget.LuminaWidget
import androidx.glance.appwidget.updateAll
import kotlinx.coroutines.launch

enum class Screen {
    Home,
    Preferences,
    WidgetStudio,
    SavedQuotes
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val quoteRepository = QuoteRepository(this)
        val widgetStyleRepository = WidgetStyleRepository(this)
        val savedQuotesRepository = SavedQuotesRepository(this)
        
        // Check if we were launched from the widget's edit button
        val initialScreen = if (intent?.getBooleanExtra("open_widget_studio", false) == true) {
            Screen.WidgetStudio
        } else {
            Screen.Home
        }

        setContent {
            var currentScreen by remember { mutableStateOf(initialScreen) }
            var currentQuote by remember { mutableStateOf(quoteRepository.getRandomQuote()) }
            var widgetStyle by remember { mutableStateOf(widgetStyleRepository.loadStyle()) }
            var savedQuotes by remember { mutableStateOf(savedQuotesRepository.loadSavedQuotes()) }

            LuminaTheme {
                when (currentScreen) {
                    Screen.Home -> HomeScreen(
                        quote = currentQuote,
                        isSaved = currentQuote?.let { savedQuotesRepository.isQuoteSaved(it.id) } ?: false,
                        onRefresh = { currentQuote = quoteRepository.getRandomQuote() },
                        onToggleSave = {
                            currentQuote?.let { quote ->
                                if (savedQuotesRepository.isQuoteSaved(quote.id)) {
                                    savedQuotesRepository.removeQuote(quote.id)
                                } else {
                                    savedQuotesRepository.saveQuote(quote)
                                }
                                savedQuotes = savedQuotesRepository.loadSavedQuotes()
                            }
                        },
                        onOpenPreferences = { currentScreen = Screen.Preferences },
                        onOpenSaved = { currentScreen = Screen.SavedQuotes },
                        onShare = {
                            currentQuote?.let { quote ->
                                val sendIntent: Intent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, "“${quote.text}” — ${quote.author}")
                                    type = "text/plain"
                                }
                                val shareIntent = Intent.createChooser(sendIntent, null)
                                startActivity(shareIntent)
                            }
                        }
                    )
                    Screen.Preferences -> PreferencesScreen(
                        onBack = { currentScreen = Screen.Home },
                        onNavigateToWidgetStudio = { currentScreen = Screen.WidgetStudio }
                    )
                    Screen.WidgetStudio -> WidgetStudioScreen(
                        initialStyle = widgetStyle,
                        onSave = { style ->
                            widgetStyleRepository.saveStyle(style)
                            widgetStyle = style
                            Toast.makeText(this@MainActivity, "Widget style saved!", Toast.LENGTH_SHORT).show()
                            // Update the home screen widget
                            lifecycleScope.launch {
                                LuminaWidget().updateAll(this@MainActivity)
                            }
                        },
                        onBack = { currentScreen = Screen.Preferences }
                    )
                    Screen.SavedQuotes -> SavedQuotesScreen(
                        savedQuotes = savedQuotes,
                        onRemove = { id ->
                            savedQuotesRepository.removeQuote(id)
                            savedQuotes = savedQuotesRepository.loadSavedQuotes()
                        },
                        onBack = { currentScreen = Screen.Home }
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // This is not handled by the setContent block above automatically if the activity is already running.
        // For simplicity in this demo, we'll just check it in onCreate, 
        // but normally you'd want to handle it here too if launchMode is singleTop/Task.
    }
}
