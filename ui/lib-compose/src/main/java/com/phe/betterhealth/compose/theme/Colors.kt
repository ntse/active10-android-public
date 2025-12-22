package com.phe.betterhealth.compose.theme

import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

val BhCardHeaderBackground = Color(0xFFf5f6f9)
val VeryDarkGray = Color(0xFF212121)
val StrongBlue = Color(0xFF177AC0)
val DarkCyan = Color(0xFF037D8A)

val colorScheme = lightColorScheme(
    primary = StrongBlue,
    onPrimary = Color.White,


    surfaceVariant = Color.White,
    onSurfaceVariant = VeryDarkGray,
//    secondaryVariant = DarkCyan,
//    onSecondary = Color.Black,
)
