package com.charan.yourday.presentation.settings.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SettingItem(
    label: String,
    trailingContent: @Composable (() -> Unit)? = null,
    supportingContent: @Composable (() -> Unit)? = null,
    isClickable: Boolean = false,
    onClick : () -> Unit = {}
) {
    ListItem(
        headlineContent = { Text(label) },
        trailingContent = trailingContent,
        supportingContent = supportingContent,


        modifier = Modifier
            .fillMaxWidth()
            .then(if (isClickable) Modifier.clickable { onClick() } else Modifier)


    )
}