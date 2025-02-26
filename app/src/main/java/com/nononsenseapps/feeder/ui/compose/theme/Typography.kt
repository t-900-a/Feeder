package com.nononsenseapps.feeder.ui.compose.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp

// Set of Material typography styles to start with
val Typography = Typography(
    h1 = TextStyle(
        fontWeight = FontWeight.Light,
        fontSize = 34.sp,
        letterSpacing = (-1.5).sp
    ),
    h2 = TextStyle(
        fontWeight = FontWeight.Light,
        fontSize = 30.sp,
        letterSpacing = (-0.5).sp
    ),
    h3 = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 28.sp,
        letterSpacing = 0.sp
    ),
    h4 = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 26.sp,
        letterSpacing = 0.25.sp
    ),
    h5 = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp,
        letterSpacing = 0.sp
    ),
    h6 = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp,
        letterSpacing = 0.15.sp
    ),
)

@Composable
fun LinkTextStyle(): TextStyle =
    TextStyle(
        color = MaterialTheme.colors.secondary,
        textDecoration = TextDecoration.Underline
    )

@Composable
fun FeedListItemTitleStyle(): SpanStyle =
    FeedListItemTitleTextStyle().toSpanStyle()

@Composable
fun FeedListItemTitleTextStyle(): TextStyle =
    MaterialTheme.typography.h6
        .merge(
            TextStyle(
                fontSize = 17.sp,
//                fontWeight = FontWeight.SemiBold,
            )
        )

@Composable
fun FeedListItemStyle(): TextStyle =
    MaterialTheme.typography.body1

@Composable
fun FeedListItemFeedTitleStyle(): TextStyle =
    FeedListItemDateStyle()

@Composable
fun FeedListItemDateStyle(): TextStyle =
    MaterialTheme.typography.caption.merge(
        TextStyle(
            fontWeight = FontWeight.Medium
        )
    )

@Composable
fun ReadAloudPlayerStyle(): TextStyle =
    MaterialTheme.typography.subtitle1

@Composable
fun CodeInlineStyle(): SpanStyle =
    SpanStyle(
        background = CodeBlockBackground(),
        fontFamily = FontFamily.Monospace
    )

/**
 * Has no background because it is meant to be put over a Surface which has the proper background.
 */
@Composable
fun CodeBlockStyle(): TextStyle =
    MaterialTheme.typography.body2.merge(
        SpanStyle(
            fontFamily = FontFamily.Monospace
        )
    )

@Composable
fun CodeBlockBackground(): Color =
    when (MaterialTheme.colors.isLight) {
        true -> Color.LightGray
        false -> Color.DarkGray
    }

@Composable
fun BlockQuoteStyle(): SpanStyle =
    MaterialTheme.typography.body2.toSpanStyle().merge(
        SpanStyle(
            fontWeight = FontWeight.Light
        )
    )
