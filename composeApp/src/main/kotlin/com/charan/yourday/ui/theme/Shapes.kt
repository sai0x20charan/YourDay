package com.charan.yourday.ui.theme

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ListItemShapes
import androidx.compose.material3.ShapeDefaults
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun customListItemShapes(
    indexItem: IndexItem,
    defaultShape : ListItemShapes = ListItemDefaults.shapes()
): ListItemShapes {
    val overrideShape = ShapeDefaults.Large
    val shape = when (indexItem) {
        IndexItem.FIRST -> {
            val defaultBaseShape = defaultShape.shape
            if (defaultBaseShape is CornerBasedShape) {
                defaultShape.copy(
                    shape =
                        defaultBaseShape.copy(
                            topStart = overrideShape.topStart,
                            topEnd = overrideShape.topEnd,
                        )
                )
            } else {
                defaultShape
            }
        }
        IndexItem.LAST -> {
            val defaultBaseShape = defaultShape.shape
            if (defaultBaseShape is CornerBasedShape) {
                defaultShape.copy(
                    shape =
                        defaultBaseShape.copy(
                            bottomStart = overrideShape.bottomStart,
                            bottomEnd = overrideShape.bottomEnd,
                        )
                )
            } else {
                defaultShape
            }
        }

        IndexItem.MIDDLE -> {
            defaultShape
        }

        IndexItem.FIRST_AND_LAST -> {
            defaultShape.copy(
                shape = ShapeDefaults.Large
            )


        }
    }

    return shape
}

fun <T> List<T>.indexItemFor(index: Int): IndexItem {
    return when {
        size == 1 -> IndexItem.FIRST_AND_LAST
        index == 0 -> IndexItem.FIRST
        index == size - 1 -> IndexItem.LAST
        else -> IndexItem.MIDDLE
    }
}


enum class IndexItem {
    FIRST,
    LAST,
    MIDDLE,

    FIRST_AND_LAST
}