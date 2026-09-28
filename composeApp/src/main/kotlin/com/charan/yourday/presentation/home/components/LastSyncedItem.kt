package com.charan.yourday.presentation.home.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable

fun LastSyncedItem(
    lastSynced : String,
    onRefresh : () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("Last Synced : $lastSynced", style = MaterialTheme.typography.labelSmall)
        Spacer(Modifier.weight(1f))
        IconButton(
            onClick = {
                onRefresh()
            },
            modifier = Modifier.size(20.dp)

        ) {
            Icon(Icons.Default.Refresh,"Refresh")

        }

    }
}