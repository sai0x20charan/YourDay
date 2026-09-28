package com.charan.yourday.presentation.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.charan.yourday.MR
import dev.icerock.moko.resources.compose.painterResource

@Composable

fun ErrorCard() {
    Column (modifier = Modifier.fillMaxSize().padding(top = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center){
        Image(painter = painterResource(MR.images.error),null,modifier= Modifier.padding(bottom = 20.dp).size(75.dp))
        Text("Something went wrong", style = MaterialTheme.typography.bodyLarge,modifier= Modifier.padding(bottom = 10.dp))

    }
}