package io.embry.hellowordel.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import io.embry.hellowordel.R

val headerFont = FontFamily(Font(R.font.volkorn_semibold))
val bodyFont = FontFamily(Font(R.font.nunito_sans_regular))
val italicFont = FontFamily(Font(R.font.nunito_sans_semibold_italic))

val typography = Typography(
    body1 = TextStyle(
        fontFamily = bodyFont,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    body2 = TextStyle(
        fontFamily = bodyFont,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    h1 = TextStyle(
        fontFamily = headerFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp
    ),
    h2 = TextStyle(
        fontFamily = headerFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp
    ),
    caption = TextStyle(
        fontFamily = italicFont,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    )
)