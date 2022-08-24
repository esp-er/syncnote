package com.patriker.android.syncnote.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.material.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.patriker.syncnote.routing.NotesRouter
import com.patriker.syncnote.routing.Screen
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.sp
import com.patriker.android.syncnote.ui.components.*
import com.patriker.syncnote.MainViewModel
import compose.icons.TablerIcons
import compose.icons.tablericons.SquarePlus
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt




@ExperimentalMaterialApi
@Composable
fun NotesScreen(viewModel: MainViewModel) {

    /*
    val configuration = LocalConfiguration.current
    val densityDpi = resources.displayMetrics.densityDpi

    val screenWidth = configuration.screenWidthDp * (densityDpi / 160f) //Convert Dp to pixel values
    val screenHeight = configuration.screenHeightDp * (densityDpi / 160f)
    *
    /

     */

    val configuration = LocalConfiguration.current
    val drawerWidth  = with(LocalDensity.current) { configuration.screenWidthDp.dp.toPx() }  / 1.4f
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

    val fabPos: Offset by viewModel.fabPos.observeAsState(viewModel.fabPos.value ?: Offset(0f,0f))
    val isConnected: Boolean by viewModel.isSyncing.observeAsState(initial = false)
    val isPaired: Boolean by viewModel.isPaired.observeAsState(initial = false);

    BackHandler(
        onBack = {
            if (scaffoldState.drawerState.isOpen) { //comment: useful back behavior pattern with the drawer
                coroutineScope.launch { scaffoldState.drawerState.close() }
            } else {
                NotesRouter.navigateTo(Screen.Notes)
            }
        }
    )



    Scaffold (
        topBar =
        {
            Column {
                TopAppBar(
                    modifier = Modifier.heightIn(50.dp,50.dp),
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
                            coroutineScope.launch{
                                scaffoldState.drawerState.close()
                            }
                        },
                        isConnected = isConnected,
                        isPaired = isPaired,
                        viewModel::resetPairing
                    )
        },
        drawerScrimColor = androidx.compose.ui.graphics.Color.Black.copy(alpha=0.4f),
        drawerBackgroundColor = MaterialTheme.colors.background,
        drawerShape = CustomDrawerShape(drawerWidth, drawerHeight),
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            var initY by remember { mutableStateOf(0f)}
            var initX by remember { mutableStateOf(0f )}
            var buttonWidth by remember { mutableStateOf(0)}
            var buttonHeight by remember { mutableStateOf(0)}

            FloatingActionButton(
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .heightIn(42.dp)
                    .onGloballyPositioned { coords ->
                        val pos = coords.positionInRoot()
                        initX =
                            pos.x //TODO: find out a better way to figure out "screen bounds" initially
                        initY = pos.y
                        buttonWidth = coords.size.width
                        buttonHeight = coords.size.height
                    }
                    .offset { IntOffset(fabPos.x.roundToInt(), fabPos.y.roundToInt()) }
                    .pointerInput(Unit) {
                        coroutineScope.launch { //TODO: handle offset in portrait/landscape
                            detectDragGestures(
                                onDrag = { change, offset ->
                                    change.consumeAllChanges()
                                    val (deltaX, deltaY) = offset

                                    val newx = when (fabPos.x + deltaX) {
                                        in (-initX + (buttonWidth / 3.0f))..0f -> fabPos.x + deltaX
                                        else -> fabPos.x
                                    }
                                    val newy = when (fabPos.y + deltaY) {
                                        in (-initY / 1.5f)..0f -> fabPos.y + deltaY
                                        else -> fabPos.y
                                    }

                                    viewModel.setFabPos(Offset(newx, newy))
                                }
                            )
                        }
                    },
                onClick = { viewModel.onCreateNewNoteClick() },
                contentColor = MaterialTheme.colors.onPrimary,
                content = {
                    Row(modifier = Modifier.padding(horizontal = 12.dp)){
                        Icon(
                            imageVector = TablerIcons.SquarePlus,
                                    contentDescription = "Add Note Button" ,
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .size(18.dp)
                        )
                        Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                        Text("New",
                            fontSize = 14.sp,
                            modifier = Modifier.align(Alignment.CenterVertically)
                            )
                    }
                }
            )
        },
        content = { pad ->
                NotesList(
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
