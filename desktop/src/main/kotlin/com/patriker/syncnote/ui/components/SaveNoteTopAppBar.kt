package com.patriker.syncnote.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import compose.icons.Octicons
import compose.icons.octicons.*

@Composable
fun SaveNoteTopAppBar(
    title: String = "New Note",
    enableTrash: Boolean,
    enablePermaDelete: Boolean,
    onBackClick: () -> Unit,
    onSaveNoteClick: () -> Unit,
    onOpenColorPickerClick: () -> Unit,
    onDeleteNoteClick: () -> Unit,
    onPermaDeleteNote: () -> Unit,
    onRestoreNote: () -> Unit
) {

    TopAppBar(
        modifier = Modifier.heightIn(40.dp, 40.dp),
        title = {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.W500
            )
        },

        navigationIcon = {
            Box(modifier = Modifier.size(36.dp), contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Octicons.ArrowLeft24,
                    contentDescription = "Save Note Button",
                    tint = MaterialTheme.colors.onPrimary,
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple(
                            bounded = false,
                            radius = 16.dp
                        ), // You can also change the color and radius of the ripple
                        onClick = onBackClick
                    )
                )
            }
        },
        actions = {
            Box(Modifier.size(36.dp).fillMaxHeight(), contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Octicons.Check24,
                    tint = MaterialTheme.colors.onPrimary,
                    contentDescription = "Save Note",
                    modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(
                        bounded = false,
                        radius = 16.dp
                    ), // You can also change the color and radius of the ripple
                    onClick = onSaveNoteClick
                    )
                )
            }
            if (enableTrash) {
                Box(Modifier.size(36.dp).fillMaxHeight(), contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Octicons.Inbox24,
                        tint = MaterialTheme.colors.onPrimary,
                        contentDescription = "Trash Note Button",
                        modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple(
                            bounded = false,
                            radius = 16.dp
                        ), // You can also change the color and radius of the ripple
                        onClick = onBackClick
                        )
                    )
                }
            }
            if (enablePermaDelete) {
                //Restore Function
                IconButton(onClick = onRestoreNote) {
                    Icon(
                        imageVector = Octicons.People24,
                        tint = MaterialTheme.colors.onPrimary,
                        contentDescription = "Restore Note Button"
                    )

                }

                IconButton(onClick = onPermaDeleteNote) {
                    Icon(
                        imageVector = Octicons.Trash24,
                        tint = MaterialTheme.colors.onPrimary,
                        contentDescription = "Permanently Delete Note Button"
                    )
                }
            }
        },
        backgroundColor = MaterialTheme.colors.background

    )
}