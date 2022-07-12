package com.patriker.syncnote.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.SnackbarDefaults.backgroundColor
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
//import androidx.compose.runtime.livedata.observeAsState
import com.raywenderlich.jetnotes.routing.Screen

//import com.raywenderlich.android.jetnotes.ui.components.TopAppBar
import com.patriker.syncnote.ui.components.*
import com.raywenderlich.jetnotes.MainViewModel
import compose.icons.TablerIcons
import compose.icons.tablericons.SquarePlus
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt




@ExperimentalMaterialApi
@Composable
fun NotesScreen(viewModel: MainViewModel) {

    //val configuration = LocalConfiguration.current

    //this delegate unwraps State<List<NoteModel>> into regular List<NoteModel>
    val scaffoldState = rememberScaffoldState() //remembers drawer and snackbar state
    val dw = DrawerState(DrawerValue.Closed, { false })

    val isConnected = viewModel.isSyncing //TODO: change to observable (reactive) value
    Box(modifier = Modifier.background(MaterialTheme.colors.background)) {
        Column {
            TopBar(viewModel::onCreateNewNoteClick)
            //horLineSeparator()
            TopTabBar(initState = 0) //Tabs
            NotesList( // here
                notes = viewModel.notes,
                onNoteCheckedChange = { viewModel.onNoteCheckedChange(it) },
                onEditNote = { viewModel.onNoteClick(it) },
                onRestoreNote = { viewModel.restoreNoteFromArchive(it) },
                onArchiveNote = { viewModel.archiveNote(it) },
                onDeleteNote = { viewModel.permaDeleteNote(it) },
                onTogglePin = { viewModel.togglePin(it) },
                isArchive = false,
                onSnackMessage = {}
            )
        }
    }

/*
    Scaffold ( //TODO: remove scaffold and change this from scaffold to simple layout on desktop
        topBar =
        {
            Column {

            }
        },
        scaffoldState = ScaffoldState(dw, scaffoldState.snackbarHostState),
        snackbarHost = {scaffoldState.snackbarHostState},
        bottomBar = { SnackbarHost(
                        hostState = scaffoldState.snackbarHostState,
                        snackbar = { data ->
                            Snackbar(
                                snackbarData = data,
                                contentColor = MaterialTheme.colors.primary,
                                backgroundColor =  MaterialTheme.colors.surface
                            )
                        })
            
                    },
        drawerGesturesEnabled = false,
        drawerContent = {},
        drawerScrimColor = androidx.compose.ui.graphics.Color.Black.copy(alpha=0.4f),
        //drawerShape = CustomDrawerShape(drawerWidth, drawerHeight),
        //floatingActionButtonPosition = FabPosition.End,
        content = {

            }
    )*/
}
