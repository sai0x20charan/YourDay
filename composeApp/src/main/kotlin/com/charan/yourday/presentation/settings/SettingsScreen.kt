package com.charan.yourday.presentation.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Thermostat
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.charan.yourday.MR
import com.charan.yourday.Todoist
import com.charan.yourday.presentation.common.CustomDropDown
import com.charan.yourday.presentation.common.CustomListItem
import com.charan.yourday.presentation.common.toScreenContentPadding
import com.charan.yourday.presentation.settings.components.SectionHeader
import com.charan.yourday.ui.theme.IndexItem
import dev.icerock.moko.resources.compose.painterResource

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingsScreen(
    component: SettingsScreenComponent
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val state by component.settingsState.collectAsState()

    Scaffold(
        topBar = {
            LargeFlexibleTopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = { component.onEvent(SettingsEvents.onBack) }, shapes = IconButtonDefaults.shapes()) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = padding.toScreenContentPadding()
        ) {

            item {
                SectionHeader(title = "Preferences")
                CustomListItem(
                    indexItem = IndexItem.FIRST_AND_LAST,
                    leadingContent = {
                        Icon(Icons.Outlined.Thermostat, contentDescription = "Temperature Units")
                    },
                    headLineContent = {
                        Text("Temperature Units")
                    },
                    onClick = { component.onEvent(SettingsEvents.ShowDropdownMenu(true)) },
                    trailingContent = {
                        TextButton(
                            onClick = { component.onEvent(SettingsEvents.ShowDropdownMenu(true)) },
                            shapes = ButtonDefaults.shapes()
                        ) {
                            state.weatherUnits?.let { Text(it) }
                            Icon(Icons.Filled.ArrowDropDown, contentDescription = "Select unit")
                        }

                        CustomDropDown(
                            items = state.dropDownItems,
                            selectedItem = state.dropDownItems.find { it.title == state.weatherUnits },
                            isExpanded = state.showDropDown,
                            onDismiss = { component.onEvent(SettingsEvents.ShowDropdownMenu(false)) },
                            onItemSelected = { item, _ ->
                                component.onEvent(SettingsEvents.ShowDropdownMenu(false))
                                component.onEvent(SettingsEvents.OnChangeWeatherUnits(item.title))
                            }
                        )
                    }
                )
            }

            item {
                SectionHeader(title = "Integrations")
                CustomListItem(
                    indexItem = IndexItem.FIRST_AND_LAST,
                    leadingContent = {
                        Image(
                            painter = painterResource(MR.images.Todoist),
                            contentDescription = "Todoist",
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    headLineContent = {
                        Text("Todoist")
                    },

                    trailingContent = {
                        TextButton(
                            onClick = { component.onEvent(SettingsEvents.TodoConnect) },
                            shapes = ButtonDefaults.shapes()
                        ) {
                            val buttonText = if (state.isTodoistConnected == false) "Connect" else "Disconnect"
                            val buttonColor = if (state.isTodoistConnected == false) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                            Text(buttonText, color = buttonColor)
                        }
                    }
                )
            }

            item {
                SectionHeader(title = "About")

                CustomListItem(
                    indexItem = IndexItem.FIRST,
                    leadingContent = {
                        Icon(Icons.Outlined.Description, contentDescription = "Open source licenses")
                    },
                    headLineContent = {
                        Text("Open source licenses")
                    },
                    onClick = {
                        component.onEvent(SettingsEvents.OnLicenseNavigate)
                    }
                )
                CustomListItem(
                    indexItem = IndexItem.LAST,
                    leadingContent = {
                        Icon(Icons.Outlined.Info, contentDescription = "App version")
                    },
                    headLineContent = {
                        Text("App version")
                    },
                    trailingContent = {
                        Text(state.appVersion ?: "", style = MaterialTheme.typography.bodyMedium)
                    }
                )
            }
        }
    }
}
