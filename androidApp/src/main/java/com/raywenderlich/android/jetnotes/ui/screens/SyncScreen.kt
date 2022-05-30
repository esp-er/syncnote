package com.raywenderlich.android.jetnotes.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.material.Text
import androidx.compose.ui.unit.dp
import com.raywenderlich.jetnotes.routing.NotesRouter
import com.raywenderlich.jetnotes.routing.Screen

//import com.raywenderlich.android.jetnotes.ui.components.TopAppBar
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.sp
import com.raywenderlich.android.jetnotes.ui.components.*
import com.raywenderlich.jetnotes.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@ExperimentalMaterialApi
@Composable
fun SyncScreen(viewModel: MainViewModel) {

    /*
    val configuration = LocalConfiguration.current
    val densityDpi = resources.displayMetrics.densityDpi

    val screenWidth = configuration.screenWidthDp * (densityDpi / 160f) //Convert Dp to pixel values
    val screenHeight = configuration.screenHeightDp * (densityDpi / 160f)
    *
    /

     */

    val configuration = LocalConfiguration.current
    val drawerWidth  = with(LocalDensity.current) { configuration.screenWidthDp.dp.toPx() }  / 1.6f
    val drawerHeight = with(LocalDensity.current) { configuration.screenHeightDp.dp.toPx()}



    //this delegate unwraps State<List<NoteModel>> into regular List<NoteModel>
    val scaffoldState = rememberScaffoldState() //remembers drawer and snackbar state
    val coroutineScope = rememberCoroutineScope()

    fun showSnackBar(message: String) {
        coroutineScope.launch{
            val showbar = launch { scaffoldState.snackbarHostState.showSnackbar(message, duration = SnackbarDuration.Indefinite)}
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
                NotesRouter.navigateTo(Screen.Sync)
            }
        }
    )



    Scaffold (
        topBar =
        {
            Column {
                TopAppBar(
                    modifier = Modifier.heightIn(50.dp,50.dp),
                    backgroundColor = MaterialTheme.colors.secondaryVariant,
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
                                imageVector = Icons.Filled.List,
                                contentDescription = "Drawer Button"
                            )
                        }
                    }
                )
                TopTabBar(initState = 1) //Tabs
            }
        },
        scaffoldState = scaffoldState, //lets the scaffold display the correct state
        snackbarHost = {scaffoldState.snackbarHostState},
        bottomBar = { SnackbarHost(
            hostState = scaffoldState.snackbarHostState)
        },
        drawerContent = {
            AppDrawer(
                currentScreen = Screen.Sync,
                closeDrawerAction = {
                    //Drawer close
                    coroutineScope.launch{
                        scaffoldState.drawerState.close()
                    }
                }
            )
        },
        drawerShape = CustomDrawerShape(drawerWidth, drawerHeight),
        content = {
            SyncedNoteList( // here
                notes = viewModel.cachedNotes,
                onDeleteNote = { },
                onSnackMessage = ::showSnackBar
            )
        }
    )
}
