package com.example.lumina

import android.appwidget.AppWidgetManager
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
        
        // Handle widget configuration
        val appWidgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        // Check if we were launched from the widget's edit button or as a configuration activity
        val initialScreen = if (intent?.getBooleanExtra("open_widget_studio", false) == true || 
            appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
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
                        style = widgetStyle,
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

                            // If we were configuring a widget, set the result and finish
                            if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                                val resultValue = Intent().apply {
                                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                                }
                                setResult(RESULT_OK, resultValue)
                                finish()
                            } else {
                                // "Take me to the widget" - Navigate back to Home screen to see reflect changes
                                currentScreen = Screen.Home
                                
                                // Alternatively, if they literally mean the device home screen:
                                // val startMain = Intent(Intent.ACTION_MAIN)
                                // startMain.addCategory(Intent.CATEGORY_HOME)
                                // startMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                // startActivity(startMain)
                            }
                        },
                        onBack = { 
                            if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                                finish()
                            } else {
                                currentScreen = Screen.Preferences 
                            }
                        }
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
}
