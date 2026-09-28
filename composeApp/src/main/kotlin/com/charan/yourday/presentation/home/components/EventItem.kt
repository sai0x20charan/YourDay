package com.charan.yourday.presentation.home.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.charan.yourday.data.model.CalenderItems
import com.charan.yourday.utils.DateUtils

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun EventItem(calenderEvent: CalenderItems) {
    val eventColor = Color(color = calenderEvent.calenderColor?.toLong() ?: 0)
    Row(modifier = Modifier.padding(top = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        VerticalDivider(
            modifier = Modifier.padding(end = 8.dp).clip(RoundedCornerShape(8.dp)).height(30.dp),
            color = eventColor,
            thickness = 2.dp
        )

        Column(
            modifier = Modifier

        ) {
            Column {
                Text(
                    text = calenderEvent.getEventName(),
                    style = MaterialTheme.typography.titleMediumEmphasized,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${calenderEvent.getFormatedStartTime()} - ${calenderEvent.getFormatedEndTime()}",
                    style = MaterialTheme.typography.bodySmallEmphasized,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }


        }
    }
}

