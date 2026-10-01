package com.charan.yourday.presentation.home.components

import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun GrantPermissionContent(
    title : String,
    onClick : () -> Unit
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmallEmphasized
    )
    Button(onClick = {onClick()}) {
        Text("Grant Permission")
    }

}







