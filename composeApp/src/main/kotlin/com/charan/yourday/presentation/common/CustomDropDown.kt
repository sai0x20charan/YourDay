package com.charan.yourday.presentation.common

import androidx.compose.foundation.layout.size
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed

@Composable
fun CustomDropDown(
    items: List<DropDownItem>,
    selectedItem: DropDownItem? = null,
    onItemSelected: (DropDownItem, Int) -> Unit,
    isExpanded: Boolean = false,
    onDismiss: () -> Unit = { },
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh
) {
    DropdownMenu(
        expanded = isExpanded,
        onDismissRequest = {
            onDismiss()
        },
        containerColor = containerColor
    ) {
        items.fastForEachIndexed { index, item ->
            DropdownMenuItem(
                text = {
                    Text(text = item.title)
                },
                trailingIcon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                },
                onClick = {
                    onDismiss()
                    onItemSelected(item, index)
                }
            )
        }
    }
}

data class DropDownItem(
    val title: String,
    val icon: ImageVector
)
