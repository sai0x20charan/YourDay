package com.charan.yourday.presentation.common

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedListItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.charan.yourday.ui.theme.IndexItem
import com.charan.yourday.ui.theme.customListItemShapes

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CustomListItem(
    modifier: Modifier = Modifier,
    indexItem: IndexItem,
    headLineContent: @Composable () -> Unit,
    supportingContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    leadingContent: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    verticalAlignment: Alignment.Vertical = ListItemDefaults.verticalAlignment()
) {
    SegmentedListItem(
        content = headLineContent,
        onClick = { onClick?.invoke() },
        supportingContent = supportingContent,
        trailingContent = trailingContent,
        leadingContent = leadingContent,
        shapes = customListItemShapes(indexItem),
        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        modifier = modifier.padding(1.dp),
        contentPadding = contentPadding,
        verticalAlignment = verticalAlignment
    )
}
