package com.patriker.android.syncnote.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.vector.ImageVector
import com.patriker.android.syncnote.theme.SyncNoteTheme
import com.patriker.syncnote.theme.ThemeSettingsShared
import com.patriker.syncnote.routing.NotesRouter
import com.patriker.syncnote.routing.Screen
import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Archive
import compose.icons.TablerIcons
import compose.icons.tablericons.*

@Composable
fun AppDrawer(
    currentScreen: Screen,
    closeDrawerAction: () -> Unit,
    isConnected: Boolean = false
) {
    Column(modifier = Modifier.fillMaxHeight().fillMaxWidth(0.8f)) {
        AppDrawerHeader()
        Divider(color = MaterialTheme.colors.onSurface.copy(alpha = .2f))
        ScreenNavigationButton(
            icon = TablerIcons.Notebook,
            label = "Notes",
            isSelected = currentScreen == Screen.Notes,
            onClick = {
                NotesRouter.navigateTo(Screen.Notes)
            },
        )
        ScreenNavigationButton(
            icon = TablerIcons.DeviceLaptop,
            label = if(!isConnected) "Desktop Notes" else "Desktop Notes (Connected)",
            isSelected = currentScreen == Screen.Synced,
            onClick = {
                NotesRouter.navigateTo(Screen.Synced)
                closeDrawerAction()
            }
        )
        ScreenNavigationButton(
            icon = Icons.Outlined.Archive,
            label = "Archive",
            isSelected = currentScreen == Screen.Archive,
            onClick = {
                NotesRouter.navigateTo(Screen.Archive)
                closeDrawerAction()
            }
        )

        SyncToggleItem()
        LightDarkThemeItem()
    }
}

@Preview
@Composable
fun AppDrawerPreview() {
    SyncNoteTheme {
        AppDrawer(Screen.Notes, {})//note: empty function passed
                                   // as closedraweraction not needed for preview
    }
}
@Preview
@Composable
fun ScreenNavigationButtonPreview() {
    SyncNoteTheme {
        ScreenNavigationButton(
            icon = Icons.Filled.Home,
            label = "Notes",
            isSelected = true,
            onClick = { }
        )
    }
}

@Composable
private fun ScreenNavigationButton(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val colors = MaterialTheme.colors
    val imageAlpha = if (isSelected) {
        1f
    } else {
        0.6f
    }
    val textColor = if (isSelected) {
        colors.primary
    } else {
        colors.onSurface.copy(alpha = 0.6f)
    }
    val backgroundColor = if (isSelected) {
        colors.primary.copy(alpha = 0.12f)
    } else {
        colors.surface //note "colors.surface"
    }

    Surface( // 1
        modifier = Modifier
            .fillMaxWidth(0.95f)
            .padding(start = 8.dp, end = 8.dp, top = 8.dp),
        color = backgroundColor,
        shape = MaterialTheme.shapes.small
    ) {
        Row( // 2
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable(onClick = onClick)
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            Image(
                imageVector = icon,
                contentDescription = "Screen Navigation Button",
                colorFilter = ColorFilter.tint(textColor),
                alpha = imageAlpha
            )
            Spacer(Modifier.width(16.dp)) // 3
            Text(
                text = label,
                style = MaterialTheme.typography.body2,
                color = textColor,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun AppDrawerHeader() {
    Row(modifier = Modifier.fillMaxWidth()) {
        Image(
            imageVector = Icons.Filled.Menu,
            contentDescription = "Drawer Header Icon",
            colorFilter = ColorFilter
                .tint(MaterialTheme.colors.onSurface),
            modifier = Modifier.padding(16.dp)
        )
        Text(
            text = "SyncNote",
            modifier = Modifier.align(alignment = Alignment.CenterVertically)
        )
    }
}

@Preview
@Composable
fun AppDrawerHeaderPreview() {
    SyncNoteTheme {
        AppDrawerHeader()
    }
}
@Composable
private fun SyncToggleItem(modifier: Modifier = Modifier) {
    Row(
        Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(0.9f)
    ) {

        Column(modifier = Modifier.align(Alignment.CenterVertically).weight(1f)) {
            Row {
                Text(
                    text = "Automatic Sync on Local Network",
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier
                        .padding(8.dp)
                        //.align(alignment = Alignment.Start)
                )
                Icon(
                    imageVector = TablerIcons.Refresh,
                    "Sync Icon",
                    modifier = Modifier
                        .size(16.dp)
                        .align(Alignment.CenterVertically)
                )
            }
        }

        Column(modifier = Modifier.align(Alignment.CenterVertically).weight(1f)) {
            Switch( //TODO: implement toggling for sync
                checked = false,
                onCheckedChange = { },
                modifier = Modifier
                    .padding(start = 8.dp, end = 32.dp)
                    .align(alignment = Alignment.End)
            )
        }
    }
}

@Preview
@Composable
fun SyncToggleItemPreview() {
    SyncNoteTheme {
        SyncToggleItem()
    }
}


@Composable
private fun LightDarkThemeItem() {
    Row(
        Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(0.9f)
    ) {
        Column(modifier = Modifier.align(Alignment.CenterVertically).weight(1f)){
            Row{
                Text(
                    text = "Dark mode",
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier
                        .padding(8.dp)

                )
                Icon(
                    imageVector = TablerIcons.Moon,
                    "Night Icon",
                    modifier = Modifier
                        .size(16.dp)
                        .align(Alignment.CenterVertically)
                )
            }
        }
        Column (modifier = Modifier.align(Alignment.CenterVertically).weight(1f)){
            Switch(
                checked = ThemeSettingsShared.isDarkThemeEnabled,
                onCheckedChange = { ThemeSettingsShared.isDarkThemeEnabled = it },
                modifier = Modifier
                    .padding(start = 8.dp, end = 32.dp)
                    .align(alignment = Alignment.End)
            )
        }
    }
}
@Preview
@Composable
fun LightDarkThemeItemPreview() {
    SyncNoteTheme {
        LightDarkThemeItem()
    }
}