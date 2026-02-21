package com.example.lumina.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.ExperimentalSerializationApi

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class WidgetStyle(
    val cornerRadius: Int = 16,
    val backgroundType: BackgroundType = BackgroundType.SOLID,
    val backgroundColor: Int = 0xFFF8F1E7.toInt(), // SoftCream
    val backgroundGradient: GradientPreset = GradientPreset.SUNRISE,
    val transparency: Float = 1.0f,
    val appFont: AppFont = AppFont.PLAYFAIR,
    val fontSize: Int = 16,
    val adaptiveColor: Boolean = true,
    val showAuthor: Boolean = true,
    val showLogo: Boolean = false
)

@Serializable
enum class AppFont {
    PLAYFAIR,
    MONTSERRAT,
    SERIF,
    SANS_SERIF
}

@Serializable
enum class BackgroundType {
    SOLID, GRADIENT
}

@Serializable
enum class GradientPreset {
    SUNRISE, MIDNIGHT, EMBER, AURORA
}
