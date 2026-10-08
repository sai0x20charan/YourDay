package com.charan.yourday.presentation.common

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CustomLargeFlexibleTopAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: (@Composable () -> Unit)? = null,
    showBackButton: Boolean = false,
    onBackClick: () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior? = null,
    colors: TopAppBarColors = TopAppBarDefaults.topAppBarColors(),
) {
    LargeFlexibleTopAppBar(
        title = title,
        subtitle = subtitle,
        modifier = modifier,
        navigationIcon = {
            if (showBackButton) {
                BackButton(onClick = onBackClick)
            }
        },
        actions = actions,
        scrollBehavior = scrollBehavior,
        colors = colors,
    )
}
