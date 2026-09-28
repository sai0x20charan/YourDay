package com.charan.yourday.presentation.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.charan.yourday.MR
import dev.icerock.moko.resources.compose.painterResource

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable

fun NoTodoItem() {
    Column (modifier = Modifier.fillMaxSize().padding(top = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center){
        Image(painter = painterResource(MR.images.notasks),null,modifier=Modifier.padding(bottom = 20.dp))
        Text("No tasks today", style = MaterialTheme.typography.labelLargeEmphasized,modifier=Modifier.padding(bottom = 10.dp))
        Text("Enjoy your free day", style = MaterialTheme.typography.labelSmallEmphasized)


    }
}