package com.charan.yourday.presentation.home.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TodoLoadingItem() {
    Row (modifier = Modifier.fillMaxWidth()){
        Text("Fetching Todo Items")
        Spacer(Modifier.weight(1f))
        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)

    }
}