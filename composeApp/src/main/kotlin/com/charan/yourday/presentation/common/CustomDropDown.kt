package com.charan.yourday.presentation.common

import androidx.compose.foundation.layout.size
import androidx.compose.material3.DropdownMenuGroup
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.DropdownMenuPopup
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CustomDropDown(
    items: List<DropDownItem>,
    selectedItem: DropDownItem? = null,
    onItemSelected: (DropDownItem, Int) -> Unit,
    isExpanded : Boolean = false,
    onDismiss : () -> Unit = { },
    containerColor : Color = MaterialTheme.colorScheme.surfaceContainerHigh
) {
    DropdownMenuPopup(
        expanded = isExpanded,
        onDismissRequest = {
            onDismiss()
        },

        ) {
        DropdownMenuGroup(
            shapes = MenuDefaults.groupShape(0, 1),
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
                    shapes = MenuDefaults.itemShape(index, items.size),
                    onCheckedChange = {
                        onItemSelected(item,index)
                    },
                    checked = item == selectedItem,
                    colors = MenuDefaults.selectableItemColors().copy(
                        containerColor = containerColor
                    )
                )
            }
        }

    }


}

data class DropDownItem(
    val title : String,
    val icon : ImageVector
)