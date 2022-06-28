package com.patriker.syncnote.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.material.Text
import androidx.compose.ui.unit.dp
import com.raywenderlich.jetnotes.routing.NotesRouter
import com.raywenderlich.jetnotes.routing.Screen

//import com.raywenderlich.android.jetnotes.ui.components.TopAppBar
import androidx.compose.material.TopAppBar
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.sp
import com.patriker.syncnote.ui.components.*
import com.raywenderlich.jetnotes.MainViewModel
import compose.icons.Octicons
import compose.icons.octicons.ThreeBars16
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch



@ExperimentalMaterialApi
@Composable
fun SyncScreen(viewModel: MainViewModel, isHost: Boolean = false) {


    val isConnected: Boolean = viewModel.isSyncing
    val syncingHost: String = "archlinux"

    //this delegate unwraps State<List<NoteModel>> into regular List<NoteModel>
    val scaffoldState = rememberScaffoldState() //remembers drawer and snackbar state
    val coroutineScope = rememberCoroutineScope()

    fun snackBarMessage(message: String) {
        coroutineScope.launch{
            val showbar = launch {
                scaffoldState.snackbarHostState.showSnackbar(
                    message,
                    "Hide",
                    duration = SnackbarDuration.Short
                )
            }
        }
    }

    fun snackBarShort(message: String) {
        coroutineScope.launch{
            val showbar = launch {
                scaffoldState.snackbarHostState.showSnackbar(message,
                    "Hide",
                    SnackbarDuration.Indefinite)
            }
            delay(2000)
            showbar.cancel()
        }
    }

    if(!viewModel.isPaired){
        HostPairWidget()
    }
    else {
        Scaffold(
            topBar = {
                Box()
                {
                    Column {
                        TopAppBar(
                            modifier = Modifier.heightIn(50.dp, 50.dp),
                            //.border(BorderStroke(0.4.dp, MaterialTheme.colors.primaryVariant), shape = RectangleShape),
                            backgroundColor = MaterialTheme.colors.background,
                            title = {
                                Text(
                                    text = "Open Notes",
                                    fontSize = 18.sp
                                )
                            },
                            navigationIcon = {
                                IconButton(
                                    onClick = {
                                        coroutineScope.launch {
                                            scaffoldState.drawerState.open()
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Octicons.ThreeBars16,
                                        contentDescription = "Drawer Button"
                                    )
                                }
                            }
                        )
                        //horLineSeparator()
                        TopTabBar(initState = 1, isConnected) //Tabs
                    }
                }
            },
            scaffoldState = scaffoldState, //lets the scaffold display the correct state
            snackbarHost = { scaffoldState.snackbarHostState },
            bottomBar = {
                SnackbarHost(
                    hostState = scaffoldState.snackbarHostState
                )
            },
            drawerContent = {
                AppDrawer(
                    currentScreen = Screen.Sync,
                    closeDrawerAction = {
                        //Drawer close
                        coroutineScope.launch {
                            scaffoldState.drawerState.close()
                        }
                    },
                    isConnected = isConnected
                )
            },
            //drawerShape = CustomDrawerShape(drawerWidth, drawerHeight),
           content ={}
            /*{
                val isPaired = viewModel.hasPairedHost.value ?: false
                if (isPaired)
                    SyncedNoteList( // here
                        notes = viewModel.cachedNotes,
                        onDeleteNote = { },
                        onSnackMessage = ::snackBarShort
                    )
                else {
                    PairDeviceUI()
                }
            }*/
        )
    }
}
