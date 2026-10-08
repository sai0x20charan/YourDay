package com.charan.yourday.presentation.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun BackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String = "Back",
) {
    IconButton(
        onClick = onClick,
        modifier = modifier,
        shapes = IconButtonDefaults.shapes(),
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Default.ArrowBack,
            contentDescription = contentDescription,
        )
    }
}
