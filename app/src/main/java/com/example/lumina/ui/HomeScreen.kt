package com.example.lumina.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lumina.data.AppFont
import com.example.lumina.data.Quote
import com.example.lumina.data.WidgetStyle
import com.example.lumina.ui.theme.LuminaTheme
import com.example.lumina.ui.theme.MontserratFont
import com.example.lumina.ui.theme.PlayfairDisplayFont
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AuroraBackground() {
    val infiniteTransition = rememberInfiniteTransition(label = "aurora")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = Math.PI.toFloat() * 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "time"
    )

    val color1 = MaterialTheme.colorScheme.background
    val color2 = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)
    val color3 = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f)

    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // Subtle moving gradient centers
        val x1 = width * (0.5f + 0.3f * cos(time))
        val y1 = height * (0.5f + 0.3f * sin(time))
        
        val x2 = width * (0.5f + 0.4f * cos(time * 0.7f + 1f))
        val y2 = height * (0.5f + 0.4f * sin(time * 0.7f + 1f))

        drawRect(color = color1)

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(color2, Color.Transparent),
                center = Offset(x1, y1),
                radius = width * 0.8f
            ),
            center = Offset(x1, y1),
            radius = width * 0.8f
        )

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(color3, Color.Transparent),
                center = Offset(x2, y2),
                radius = width * 0.9f
            ),
            center = Offset(x2, y2),
            radius = width * 0.9f
        )
    }
}

@Composable
fun HomeScreen(
    quote: Quote?,
    style: WidgetStyle,
    isSaved: Boolean,
    onRefresh: () -> Unit,
    onToggleSave: () -> Unit,
    onOpenPreferences: () -> Unit,
    onOpenSaved: () -> Unit,
    onShare: () -> Unit
) {
    val currentFontFamily = when (style.appFont) {
        AppFont.PLAYFAIR -> PlayfairDisplayFont
        AppFont.MONTSERRAT -> MontserratFont
        AppFont.SERIF -> FontFamily.Serif
        AppFont.SANS_SERIF -> FontFamily.SansSerif
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AuroraBackground()

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent, // Allow aurora to show
            topBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onOpenPreferences) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = onOpenSaved) {
                        Icon(
                            imageVector = Icons.Outlined.BookmarkBorder,
                            contentDescription = "Saved Quotes",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            },
            bottomBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(bottom = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        IconButton(onClick = onRefresh) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh",
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        }
                        IconButton(onClick = onShare) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share",
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        }
                        IconButton(onClick = onToggleSave) {
                            Icon(
                                imageVector = if (isSaved) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = if (isSaved) "Unsave" else "Save",
                                tint = if (isSaved) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                AnimatedContent(
                    targetState = quote,
                    transitionSpec = {
                        (fadeIn(animationSpec = tween(1000)) + slideInVertically(
                            animationSpec = tween(1000),
                            initialOffsetY = { it / 2 }
                        )).togetherWith(fadeOut(animationSpec = tween(500)))
                    },
                    label = "QuoteRefreshAnimation"
                ) { targetQuote ->
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = targetQuote?.let { "“${it.text}”" } ?: "Loading...",
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontFamily = currentFontFamily
                            ),
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Box(
                            modifier = Modifier
                                .width(40.dp)
                                .height(1.dp)
                                .background(MaterialTheme.colorScheme.secondary)
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = targetQuote?.author?.uppercase() ?: "",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontFamily = currentFontFamily
                            ),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    LuminaTheme {
        HomeScreen(
            quote = Quote(1, "The soul becomes dyed with the color of its thoughts.", "Marcus Aurelius"),
            style = WidgetStyle(),
            isSaved = false,
            onRefresh = {},
            onToggleSave = {},
            onOpenPreferences = {},
            onOpenSaved = {},
            onShare = {}
        )
    }
}
