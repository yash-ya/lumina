package com.example.lumina.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lumina.data.AppFont
import com.example.lumina.data.BackgroundType
import com.example.lumina.data.GradientPreset
import com.example.lumina.data.WidgetStyle
import com.example.lumina.ui.theme.LuminaTheme
import com.example.lumina.ui.theme.MontserratFont
import com.example.lumina.ui.theme.PlayfairDisplayFont
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WidgetStudioScreen(
    initialStyle: WidgetStyle,
    onSave: (WidgetStyle) -> Unit,
    onBack: () -> Unit
) {
    var style by remember { mutableStateOf(initialStyle) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Widget Studio", style = MaterialTheme.typography.titleMedium) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Top Half: Live Preview
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                WidgetPreview(style = style)
            }

            // Bottom Half: Controls
            Column(
                modifier = Modifier
                    .weight(1.3f)
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                var selectedTab by remember { mutableIntStateOf(0) }
                val tabs = listOf("Style", "Typography", "Elements")

                PrimaryTabRow(selectedTabIndex = selectedTab) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(title) }
                        )
                    }
                }

                Box(modifier = Modifier.weight(1f)) {
                    when (selectedTab) {
                        0 -> StyleTab(style, onStyleChange = { style = it })
                        1 -> TypographyTab(style, onStyleChange = { style = it })
                        2 -> ElementsTab(style, onStyleChange = { style = it })
                    }
                }

                Button(
                    onClick = { onSave(style) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Save Widget Style")
                }
            }
        }
    }
}

@Composable
fun WidgetPreview(style: WidgetStyle) {
    val backgroundBrush = if (style.backgroundType == BackgroundType.GRADIENT) {
        getGradientBrush(style.backgroundGradient)
    } else {
        Brush.linearGradient(listOf(Color(style.backgroundColor), Color(style.backgroundColor)))
    }

    Surface(
        modifier = Modifier
            .width(300.dp)
            .height(180.dp),
        shape = RoundedCornerShape(style.cornerRadius.dp),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundBrush, alpha = style.transparency)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "“The soul becomes dyed with the color of its thoughts.”",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontFamily = when (style.appFont) {
                            AppFont.PLAYFAIR -> PlayfairDisplayFont
                            AppFont.MONTSERRAT -> MontserratFont
                            AppFont.SERIF -> FontFamily.Serif
                            AppFont.SANS_SERIF -> FontFamily.SansSerif
                        },
                        fontSize = style.fontSize.sp,
                        textAlign = TextAlign.Center
                    ),
                    color = if (style.adaptiveColor) Color.White else MaterialTheme.colorScheme.onSurface
                )
                if (style.showAuthor) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "MARCUS AURELIUS",
                        style = MaterialTheme.typography.labelSmall,
                        color = (if (style.adaptiveColor) Color.White else MaterialTheme.colorScheme.onSurface).copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
fun StyleTab(style: WidgetStyle, onStyleChange: (WidgetStyle) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Column {
            Text("Background Type", style = MaterialTheme.typography.labelMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
                FilterChip(
                    selected = style.backgroundType == BackgroundType.SOLID,
                    onClick = { onStyleChange(style.copy(backgroundType = BackgroundType.SOLID)) },
                    label = { Text("Solid") }
                )
                FilterChip(
                    selected = style.backgroundType == BackgroundType.GRADIENT,
                    onClick = { onStyleChange(style.copy(backgroundType = BackgroundType.GRADIENT)) },
                    label = { Text("Gradient") }
                )
            }
        }

        if (style.backgroundType == BackgroundType.GRADIENT) {
            Column {
                Text("Aurora Presets", style = MaterialTheme.typography.labelMedium)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    GradientPreset.entries.forEach { preset ->
                        GradientCircle(
                            preset = preset,
                            isSelected = style.backgroundGradient == preset,
                            onClick = { onStyleChange(style.copy(backgroundGradient = preset)) }
                        )
                    }
                }
            }
        } else {
            Column {
                Text("Color Palette", style = MaterialTheme.typography.labelMedium)
                val colors = listOf(
                    Color(0xFFF8F1E7), // SoftCream
                    Color(0xFF1B263B), // MidnightBlue
                    Color(0xFFD4AF37), // WarmGold
                    Color(0xFFB87333), // MutedCopper
                    Color(0xFFE0E0E0), // LightGray
                    Color(0xFF212121)  // DarkGray
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    colors.forEach { color ->
                        ColorCircle(
                            color = color,
                            isSelected = style.backgroundColor == color.toArgb(),
                            onClick = { onStyleChange(style.copy(backgroundColor = color.toArgb())) }
                        )
                    }
                }
            }
        }

        Column {
            Text("Transparency: ${(style.transparency * 100).toInt()}%", style = MaterialTheme.typography.labelMedium)
            Slider(
                value = style.transparency,
                onValueChange = { onStyleChange(style.copy(transparency = it)) },
                valueRange = 0.1f..1.0f
            )
        }

        Column {
            Text("Corner Radius: ${style.cornerRadius}dp", style = MaterialTheme.typography.labelMedium)
            Slider(
                value = style.cornerRadius.toFloat(),
                onValueChange = { onStyleChange(style.copy(cornerRadius = it.toInt())) },
                valueRange = 0f..40f
            )
        }
    }
}

@Composable
fun ColorCircle(color: Color, isSelected: Boolean, onClick: () -> Unit) {
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
        label = "BorderColor"
    )

    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .border(2.dp, borderColor, CircleShape)
            .padding(4.dp)
            .clip(CircleShape)
            .background(color)
            .clickable { onClick() }
    )
}

@Composable
fun GradientCircle(preset: GradientPreset, isSelected: Boolean, onClick: () -> Unit) {
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
        label = "BorderColor"
    )

    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .border(2.dp, borderColor, CircleShape)
            .padding(4.dp)
            .clip(CircleShape)
            .background(getGradientBrush(preset))
            .clickable { onClick() }
    )
}

fun getGradientBrush(preset: GradientPreset): Brush {
    return when (preset) {
        GradientPreset.SUNRISE -> Brush.linearGradient(listOf(Color(0xFFFF9A8B), Color(0xFFFF6A88), Color(0xFFFF99AC)))
        GradientPreset.MIDNIGHT -> Brush.linearGradient(listOf(Color(0xFF2C3E50), Color(0xFF000000)))
        GradientPreset.EMBER -> Brush.linearGradient(listOf(Color(0xFFf83600), Color(0xFFf9d423)))
        GradientPreset.AURORA -> Brush.linearGradient(listOf(Color(0xFF74EBD5), Color(0xFF9FACE6)))
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TypographyTab(style: WidgetStyle, onStyleChange: (WidgetStyle) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Column {
            Text("Font Family", style = MaterialTheme.typography.labelMedium)
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AppFont.entries.forEach { font ->
                    FilterChip(
                        selected = style.appFont == font,
                        onClick = { onStyleChange(style.copy(appFont = font)) },
                        label = { 
                            Text(font.name.replace("_", " ").lowercase(Locale.ROOT)
                                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }) 
                        }
                    )
                }
            }
        }

        Column {
            Text("Font Size: ${style.fontSize}sp", style = MaterialTheme.typography.labelMedium)
            Slider(
                value = style.fontSize.toFloat(),
                onValueChange = { onStyleChange(style.copy(fontSize = it.toInt())) },
                valueRange = 12f..24f
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Adaptive Color", style = MaterialTheme.typography.bodyLarge)
            Switch(
                checked = style.adaptiveColor,
                onCheckedChange = { onStyleChange(style.copy(adaptiveColor = it)) }
            )
        }
    }
}

@Composable
fun ElementsTab(style: WidgetStyle, onStyleChange: (WidgetStyle) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Show Author", style = MaterialTheme.typography.bodyLarge)
            Switch(
                checked = style.showAuthor,
                onCheckedChange = { onStyleChange(style.copy(showAuthor = it)) }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Show Lumina Logo", style = MaterialTheme.typography.bodyLarge)
            Switch(
                checked = style.showLogo,
                onCheckedChange = { onStyleChange(style.copy(showLogo = it)) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WidgetStudioPreview() {
    LuminaTheme {
        WidgetStudioScreen(initialStyle = WidgetStyle(), onSave = {}, onBack = {})
    }
}
