package com.charan.yourday.presentation.home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.charan.yourday.utils.DateUtils

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TopBarTitleContent(modifier: Modifier) {

    Column(modifier = modifier) {
        Text(DateUtils.getGreeting(),style = MaterialTheme.typography.headlineMediumEmphasized.copy(fontWeight = FontWeight.Bold))
        Spacer(Modifier.height(5.dp))
        Text(DateUtils.getDateInDDMMYYYY(), style = MaterialTheme.typography.bodyLargeEmphasized)
    }
}