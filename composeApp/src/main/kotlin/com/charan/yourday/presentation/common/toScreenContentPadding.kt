package com.charan.yourday.presentation.common

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


@Composable
fun PaddingValues.toScreenContentPadding(
    horizontal: Dp = 16.dp
): PaddingValues {
    val layoutDirection = LocalLayoutDirection.current
    return PaddingValues(
        start = this.calculateStartPadding(layoutDirection) + horizontal,
        top = this.calculateTopPadding(),
        end = this.calculateEndPadding(layoutDirection) + horizontal,
        bottom = this.calculateBottomPadding()
    )
}