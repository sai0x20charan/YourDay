package com.charan.yourday.presentation.home.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.charan.yourday.MR
import com.charan.yourday.data.model.TodoData import com.charan.yourday.utils.DateUtils.convertToMMMDYYYY
import com.charan.yourday.utils.DateUtils.convertToMMMDYYYYWithTime
import com.charan.yourday.utils.DateUtils.toMMMDYYYYWithTime
import com.mikepenz.markdown.compose.elements.MarkdownText
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichText
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditor
import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.compose.painterResource
import org.jetbrains.compose.resources.imageResource

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TodoItem(
        taskName : String,
        taskLink : String,
        todoProvider : String,
        todoProviderLogo : ImageResource,
        date : String,
        isOverDue : Boolean,
        onOpenTodo: (url : String) -> Unit
) {
        val richTextState = rememberRichTextState()
        richTextState.setMarkdown(taskName)

        Column(
                modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .clickable {
                                onOpenTodo(taskLink)
                        }


        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                RichText(
                    state = richTextState,
                    style = MaterialTheme.typography.bodyLargeEmphasized,
                    modifier = Modifier.padding(bottom = 12.dp)
                        .weight(1f)
                        .padding(end = 5.dp)
                )
                AnimatedVisibility(
                    visible = isOverDue
                ) {
                    RoundedChip(
                        title = "OverDue",
                        modifier = Modifier.background(MaterialTheme.colorScheme.errorContainer)
                            .padding(4.dp)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(
                            todoProviderLogo
                        ),
                        contentDescription = "Todo Provider",
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape),
                    )



                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = todoProvider,
                        style = MaterialTheme.typography.bodySmallEmphasized,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = date,
                    style = MaterialTheme.typography.bodySmallEmphasized,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

            }

        }
}

