package com.charan.yourday.presentation.home.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable

fun ContentElevatedCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
    title : String? = null,
    isLoading : Boolean = false,
    hasError : String? = null,
    hasContent : Boolean = true
    ) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .then(modifier)
    ) {
        Column(
            modifier = Modifier.padding(
                top = 15.dp,
                start = 10.dp,
                end = 10.dp,
                bottom = 15.dp
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (title != null) {
                Row {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLargeEmphasized.copy(fontWeight = FontWeight.Medium),
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                    Spacer(Modifier.weight(1f))
                    AnimatedVisibility(
                        visible = isLoading
                    ) {
                        CircularWavyProgressIndicator(
                            modifier = Modifier.size(25.dp),
                            waveSpeed = 1.dp,
                        )
                    }
                    AnimatedVisibility(
                        visible = hasError != null
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Error",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
            content()



        }
    }
}