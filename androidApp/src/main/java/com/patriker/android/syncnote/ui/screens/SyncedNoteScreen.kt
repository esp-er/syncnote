package com.patriker.android.syncnote.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.material.Text
import androidx.compose.ui.unit.dp
import com.patriker.syncnote.routing.NotesRouter
import com.patriker.syncnote.routing.Screen

//import com.raywenderlich.android.jetnotes.ui.components.TopAppBar
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.sp
import com.patriker.android.syncnote.ui.components.*
import com.patriker.syncnote.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch



@ExperimentalMaterialApi
@Composable
fun SyncedNoteScreen(viewModel: MainViewModel) {

    val configuration = LocalConfiguration.current
    val drawerWidth  = with(LocalDensity.current) { configuration.screenWidthDp.dp.toPx() }  / 1.4f
    val drawerHeight = with(LocalDensity.current) { configuration.screenHeightDp.dp.toPx()}

    val isConnected: Boolean by viewModel.isSyncing.observeAsState(false)
    val isPaired by viewModel.isPaired.observeAsState(false)
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
        coroutineScope.launch {
            val showbar = launch {
                scaffoldState.snackbarHostState.showSnackbar(message,
                    "Hide",
                    SnackbarDuration.Indefinite)
            }
            delay(2000)
            showbar.cancel()
        }
    }



    //val fabPos: Offset by viewModel.fabPos.observeAsState(viewModel.fabPos.value ?: Offset(0f,0f))

    BackHandler(
        onBack = {
            if (scaffoldState.drawerState.isOpen) { //comment: useful back behavior pattern with the drawer
                coroutineScope.launch { scaffoldState.drawerState.close() }
            } else {
                NotesRouter.navigateTo(Screen.Synced)
            }
        }
    )

    Scaffold (
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
                                text = "SyncNote",
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
                                    imageVector = Icons.Filled.Menu,
                                    contentDescription = "Drawer Button"
                                )
                            }
                        }
                    )
                    horLineSeparator()
                    TopTabBar(initState = 1, isConnected) //Tabs
                }
            }
        },
        scaffoldState = scaffoldState, //lets the scaffold display the correct state
        snackbarHost = {scaffoldState.snackbarHostState},
        bottomBar = { SnackbarHost(
            hostState = scaffoldState.snackbarHostState)
        },
        drawerBackgroundColor = MaterialTheme.colors.background,
        drawerShape = CustomDrawerShape(drawerWidth, drawerHeight),
        drawerContent = {
            AppDrawer(
                currentScreen = Screen.Synced,
                closeDrawerAction = {
                    //Drawer close
                    coroutineScope.launch{
                        scaffoldState.drawerState.close()
                    }
                },
                isConnected = isConnected,
                isPaired = isPaired,
                viewModel::resetPairing
            )
        },
        content = { pad ->
            //val isPaired = viewModel.isDevicePaired.value ?: false
            if(isPaired)
                SyncedNoteList( // here
                    notes = viewModel.cachedNotes,
                    onDeleteNote = { },
                    onSnackMessage = ::snackBarShort
                )
            else{
                PairDeviceUI(viewModel::attemptPairConnection)
            }
        }
    )
}
