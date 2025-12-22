package com.phe.betterhealth.compose.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

val Typography = Typography(
//    displayLarge = displayLarge,
//    displayMedium = displayMedium,
    displaySmall = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = TextUnit.Unspecified,
    ),

//    headlineLarge = headlineLarge,
//    headlineMedium = headlineMedium,
//    headlineSmall = headlineSmall,

//    titleLarge = titleLarge,
//    titleMedium = titleMedium,
//    titleSmall = titleSmall,

//    bodyLarge = bodyLarge,
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.2.sp,
    ),
//    bodySmall = bodySmall,

    labelLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.2.sp,
    ),
//    labelMedium = labelMedium
//    labelSmall = labelSmall

//    h5 = TextStyle(
//        fontWeight = FontWeight.Bold,
//        fontSize = 16.sp,
//        lineHeight = 24.sp,
//        letterSpacing = 0.2.sp,
//    ),
)
