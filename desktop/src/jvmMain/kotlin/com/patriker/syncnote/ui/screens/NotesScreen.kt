package com.patriker.syncnote.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.SnackbarDefaults.backgroundColor
import androidx.compose.runtime.*
//import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.material.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.raywenderlich.jetnotes.routing.NotesRouter
import com.raywenderlich.jetnotes.routing.Screen

//import com.raywenderlich.android.jetnotes.ui.components.TopAppBar
import androidx.compose.material.TopAppBar
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
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
    //val coroutineScope = rememberCoroutineScope()

    fun showSnackBar(message: String) {
        /*coroutineScope.launch{
            val showbar = launch { scaffoldState.snackbarHostState.showSnackbar(message, duration = SnackbarDuration.Indefinite)}
            delay(2000)
            showbar.cancel()
        }*/
    }

    val isConnected = viewModel.isSyncing //TODO: change to observable (reactive) value

    Scaffold (
        topBar =
        {
            Column {
                TopBar(viewModel::onCreateNewNoteClick)
                //horLineSeparator()
                TopTabBar(initState = 0) //Tabs
            }
        },
        scaffoldState = scaffoldState, //lets the scaffold display the correct state
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
        drawerContent = {
                    AppDrawer(
                        currentScreen = Screen.Notes,
                        closeDrawerAction = {
                            //Drawer close
                            /*coroutineScope.launch{
                                scaffoldState.drawerState.close()
                            }*/
                        },
                        isConnected = isConnected
                    )
        },
        drawerScrimColor = androidx.compose.ui.graphics.Color.Black.copy(alpha=0.4f),
        drawerBackgroundColor = MaterialTheme.colors.background,
        //drawerShape = CustomDrawerShape(drawerWidth, drawerHeight),
        //floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
        },
        content = {
                NotesList( // here
                    notes = viewModel.notes,
                    onNoteCheckedChange = { viewModel.onNoteCheckedChange(it) },
                    onEditNote = { viewModel.onNoteClick(it)},
                    onRestoreNote = {viewModel.restoreNoteFromArchive(it) },
                    onArchiveNote = { viewModel.archiveNote(it) },
                    onDeleteNote = { viewModel.permaDeleteNote(it) },
                    onTogglePin= { viewModel.togglePin(it) },
                    isArchive = false,
                    onSnackMessage = ::showSnackBar
                )
            }
    )
}
