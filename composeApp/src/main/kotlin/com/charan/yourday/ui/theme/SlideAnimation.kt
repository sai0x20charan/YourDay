package com.charan.yourday.ui.theme

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import com.arkivanov.decompose.extensions.compose.stack.animation.StackAnimator
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimator


fun slideAndFade(
    animationSpec: FiniteAnimationSpec<Float> = tween(easing = LinearEasing, durationMillis = 200),
    orientation: Orientation = Orientation.Horizontal,
    initialOffset: Int = 200,
): StackAnimator =
    stackAnimator(animationSpec = animationSpec) { factor, _, content ->
        content(
            Modifier
                .graphicsLayer(alpha = 1f - factor)
                .then(
                    when (orientation) {
                        Orientation.Horizontal -> Modifier.layout { measurable, constraints ->
                            val placeable = measurable.measure(constraints)
                            layout(placeable.width, placeable.height) {
                                placeable.placeRelative(x = (initialOffset * factor).toInt(), y = 0)
                            }
                        }
                        Orientation.Vertical -> Modifier.layout { measurable, constraints ->
                            val placeable = measurable.measure(constraints)
                            layout(placeable.width, placeable.height) {

                                placeable.placeRelative(x = 0, y = (initialOffset * factor).toInt())
                            }
                        }
                    }
                )
        )
    }
