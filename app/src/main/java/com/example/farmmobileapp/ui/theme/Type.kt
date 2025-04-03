package com.example.farmmobileapp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.example.farmmobileapp.R

val InterFamily = FontFamily(
    Font(R.font.inter_thin, FontWeight.Thin),
    Font(R.font.inter_extralight, FontWeight.ExtraLight),
    Font(R.font.inter_light, FontWeight.Light),
    Font(R.font.inter_regular),
    Font(R.font.inter_medium, FontWeight.Medium),
    Font(R.font.inter_semibold, FontWeight.SemiBold),
    Font(R.font.inter_bold, FontWeight.Bold)
)

val AppTypography = Typography()
    .copy(
        displayLarge = Typography().displayLarge.copy(
            fontFamily = InterFamily
        ),
        displayMedium = Typography().displayMedium.copy(
            fontFamily = InterFamily
        ),
        displaySmall = Typography().displaySmall.copy(
            fontFamily = InterFamily
        ),
        headlineLarge = Typography().headlineLarge.copy(
            fontFamily = InterFamily
        ),
        headlineMedium = Typography().headlineMedium.copy(
            fontFamily = InterFamily
        ),
        headlineSmall = Typography().headlineSmall.copy(
            fontFamily = InterFamily
        ),
        titleLarge = Typography().titleLarge.copy(
            fontFamily = InterFamily
        ),
        titleMedium = Typography().titleMedium.copy(
            fontFamily = InterFamily
        ),
        titleSmall = Typography().titleSmall.copy(
            fontFamily = InterFamily
        ),
        bodyLarge = Typography().bodyLarge.copy(
            fontFamily = InterFamily
        ),
        bodyMedium = Typography().bodyMedium.copy(
            fontFamily = InterFamily
        ),
        bodySmall = Typography().bodySmall.copy(
            fontFamily = InterFamily
        ),
        labelLarge = Typography().labelLarge.copy(
            fontFamily = InterFamily
        ),
        labelMedium = Typography().labelMedium.copy(
            fontFamily = InterFamily
        ),
        labelSmall = Typography().labelSmall.copy(
            fontFamily = InterFamily
        )
    )
