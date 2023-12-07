package com.example.messenger.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.messenger.R

val kufamFontFamily = FontFamily(
    Font(R.font.kufam_medium, FontWeight.Medium),
    Font(R.font.kufam_regular, FontWeight.Normal),
    Font(R.font.kufam_semi_bold, FontWeight.SemiBold)
)

val dancingScriptFontFamily = FontFamily(
    Font(R.font.dancing_script_bold, FontWeight.Bold)
)

val Typography = Typography(
    titleLarge = TextStyle(
        fontFamily = kufamFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 30.sp,
        lineHeight = 0.sp,
        platformStyle = PlatformTextStyle(
            includeFontPadding = false
        )
    ),

    displaySmall = TextStyle(
        fontFamily = kufamFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 15.sp,
        platformStyle = PlatformTextStyle(
            includeFontPadding = false
        )
    ),

    displayMedium = TextStyle(
        fontFamily = kufamFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp,
        platformStyle = PlatformTextStyle(
            includeFontPadding = false
        )
    ),

    labelSmall = TextStyle(
        fontFamily = kufamFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        platformStyle = PlatformTextStyle(
            includeFontPadding = false
        )
    ),

    bodySmall = TextStyle(
        fontFamily = kufamFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        platformStyle = PlatformTextStyle(
            includeFontPadding = false
        )
    )
)