package com.offload.companion.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors

private val OffloadSelectionColors = TextSelectionColors(
    handleColor = BrandColors.PortOfGold,
    backgroundColor = BrandColors.TealGreen.copy(alpha = 0.45f)
)

@Composable
fun OffloadTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalTextSelectionColors provides OffloadSelectionColors,
        content = content
    )
}
