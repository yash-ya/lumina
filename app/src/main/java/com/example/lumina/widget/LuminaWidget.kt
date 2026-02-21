package com.example.lumina.widget

import android.content.Context
import android.content.Intent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
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
import androidx.glance.layout.size
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

class LuminaWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val quoteRepository = QuoteRepository(context)
        val styleRepository = WidgetStyleRepository(context)
        
        val quote = quoteRepository.getRandomQuote()
        val style = styleRepository.loadStyle()

        provideContent {
            GlanceTheme {
                Box(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .padding(8.dp)
                        .background(ColorProvider(Color(style.backgroundColor))),
                    contentAlignment = Alignment.Center
                ) {
                    // Edit Button in Top Right
                    Box(
                        modifier = GlanceModifier.fillMaxSize(),
                        contentAlignment = Alignment.TopEnd
                    ) {
                        Box(
                            modifier = GlanceModifier
                                .padding(4.dp)
                                .size(24.dp)
                                .clickable(actionStartActivity<MainActivity>(
                                    actionParametersOf(ActionParameters.Key<Boolean>("open_widget_studio") to true)
                                )),
                            contentAlignment = Alignment.Center
                        ) {
                           Text(
                               text = "✎", 
                               style = TextStyle(
                                   fontSize = 14.sp,
                                   color = ColorProvider(if (style.adaptiveColor) Color.White else Color.Black)
                               )
                           )
                        }
                    }

                    Column(
                        modifier = GlanceModifier.padding(horizontal = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = quote?.let { "“${it.text}”" } ?: "Open Lumina for your daily spark",
                            style = TextStyle(
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Medium,
                                fontSize = style.fontSize.sp,
                                color = ColorProvider(if (style.adaptiveColor) Color.White else Color.Black)
                            )
                        )
                        if (style.showAuthor && quote != null) {
                            Spacer(modifier = GlanceModifier.height(4.dp))
                            Text(
                                text = quote.author.uppercase(),
                                style = TextStyle(
                                    fontSize = 10.sp,
                                    color = ColorProvider((if (style.adaptiveColor) Color.White else Color.Black).copy(alpha = 0.7f))
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

class LuminaWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = LuminaWidget()
}
