package com.example.lumina.widget

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.actionStartActivity
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.example.lumina.MainActivity
import com.example.lumina.data.QuoteRepository
import com.example.lumina.data.WidgetStyleRepository
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.clickable
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.cornerRadius
import androidx.glance.text.FontFamily
import com.example.lumina.data.AppFont
import com.example.lumina.data.BackgroundType
import com.example.lumina.data.GradientPreset

class LuminaWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val quoteRepository = QuoteRepository(context)
        val styleRepository = WidgetStyleRepository(context)
        
        val quote = quoteRepository.getRandomQuote()
        val style = styleRepository.loadStyle()

        provideContent {
            GlanceTheme {
                val contentColor = if (style.adaptiveColor) Color.White else Color.Black
                
                Box(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .appWidgetBackground()
                        .cornerRadius(style.cornerRadius.dp)
                        .then(
                            if (style.backgroundType == BackgroundType.SOLID) {
                                GlanceModifier.background(ColorProvider(Color(style.backgroundColor).copy(alpha = style.transparency)))
                            } else {
                                GlanceModifier.background(ColorProvider(getFallbackGradientColor(style.backgroundGradient).copy(alpha = style.transparency)))
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = GlanceModifier
                            .fillMaxSize()
                            .padding(12.dp)
                            .clickable(actionStartActivity<MainActivity>()),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = quote?.let { "“${it.text}”" } ?: "Open Lumina for your daily spark",
                            style = TextStyle(
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Medium,
                                fontSize = style.fontSize.sp,
                                color = ColorProvider(contentColor),
                                fontFamily = when (style.appFont) {
                                    AppFont.PLAYFAIR -> FontFamily.Serif
                                    AppFont.MONTSERRAT -> FontFamily.SansSerif
                                    AppFont.SERIF -> FontFamily.Serif
                                    AppFont.SANS_SERIF -> FontFamily.SansSerif
                                }
                            )
                        )
                        if (style.showAuthor && quote != null) {
                            Spacer(modifier = GlanceModifier.height(4.dp))
                            Text(
                                text = quote.author.uppercase(),
                                style = TextStyle(
                                    fontSize = 10.sp,
                                    color = ColorProvider(contentColor.copy(alpha = 0.7f)),
                                    fontFamily = FontFamily.SansSerif
                                )
                            )
                        }
                    }

                    // Re-add the explicit edit button for convenience, even with reconfigurable set
                    Box(
                        modifier = GlanceModifier.fillMaxSize().padding(8.dp),
                        contentAlignment = Alignment.TopEnd
                    ) {
                        Text(
                            text = "✎",
                            modifier = GlanceModifier.clickable(actionStartActivity<MainActivity>(
                                actionParametersOf(ActionParameters.Key<Boolean>("open_widget_studio") to true)
                            )),
                            style = TextStyle(
                                fontSize = 16.sp,
                                color = ColorProvider(contentColor)
                            )
                        )
                    }
                }
            }
        }
    }

    private fun getFallbackGradientColor(preset: GradientPreset): Color {
        return when (preset) {
            GradientPreset.SUNRISE -> Color(0xFFFF9A8B)
            GradientPreset.MIDNIGHT -> Color(0xFF2C3E50)
            GradientPreset.EMBER -> Color(0xFFf83600)
            GradientPreset.AURORA -> Color(0xFF74EBD5)
        }
    }
}

class LuminaWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = LuminaWidget()
}
