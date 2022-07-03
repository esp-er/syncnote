package com.patriker.syncnote.ui.screens
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
//import com.patriker.syncnote.ui.components.NoteColor
import com.raywenderlich.jetnotes.MainViewModel
import com.raywenderlich.jetnotes.domain.NoteProperty
import com.raywenderlich.jetnotes.routing.NotesRouter
import com.raywenderlich.jetnotes.routing.Screen
import compose.icons.Octicons
import compose.icons.TablerIcons
import compose.icons.octicons.*
import compose.icons.tablericons.Palette


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SaveNoteScreen(viewModel: MainViewModel, title: String = "Save Note") {

    //TODO: FIX TEMPORARY COLOR BANDAID
    //val colors = dbmap.mapColors(ColorDbModel.DEFAULT_COLORS)



    val noteEntry: NoteProperty = viewModel.noteEntry.collectAsState().value
        //.observeAsState(NoteProperty()) //noteEntry is of LiveData type
    //By observing viewModel.noteEntry, this will recompose whenever noteEntry changes.!

    // here
    /*val colors: List<ColorModel> by viewModel.colors
        .observeAsState(listOf())*/
    // here
    /*
    val trashedNotes: List<NoteProperty> by viewModel
        .notesInArchive
        .observeAsState(listOf()) //Model (Observer model state

     */

    val trashedNotes: List<NoteProperty>  = viewModel.notesInArchive.value

    val isArchivedNote by derivedStateOf {noteEntry in trashedNotes}

    Scaffold( //Creating a scaffold is easy
        topBar =
        {
            //val existingNote: Boolean = noteEntry.id != NEW_NOTE_ID //TODO: implement this with new model
            SaveNoteTopAppBar(
                title,
                enableTrash = !isArchivedNote,
                enablePermaDelete = isArchivedNote,
                onBackClick = {
                    NotesRouter.navigateTo(Screen.Notes)
                },
                onSaveNoteClick = { // here
                    viewModel.saveNote(noteEntry)
                },
                onOpenColorPickerClick = {  },
                onDeleteNoteClick = {
                    if(isArchivedNote)  viewModel.archiveNote(noteEntry) else {}
                },
                onPermaDeleteNote = {
                    viewModel.permaDeleteNote(noteEntry)
                },
                onRestoreNote = {
                    viewModel.restoreNoteFromArchive(noteEntry)
                }
            )
        },

        content = { // here
            SaveNoteContent(
                note = noteEntry,
                onNoteChange = { updateNoteEntry ->
                    viewModel.onNoteEntryChange(updateNoteEntry)
                },
                onOpenColorPickerClick = {  }
            )
        },
        backgroundColor = MaterialTheme.colors.background
    )
}

@Composable
private fun SaveNoteTopAppBar(
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
        modifier = Modifier.heightIn(40.dp,40.dp),
        title = {
            Text(
                text = title,
                color = MaterialTheme.colors.onPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.W500
            )
        },

        navigationIcon = {
            IconButton(onClick = onBackClick){
                Icon(
                    imageVector = Octicons.ArrowLeft24,
                    contentDescription = "Save Note Button",
                    tint = MaterialTheme.colors.onPrimary
                )
            }
        },
        actions = {
            IconButton(onClick = onSaveNoteClick) {
                Icon(
                    imageVector = Octicons.Check24,
                    tint = MaterialTheme.colors.onPrimary,
                    contentDescription = "Save Note"
                )
            }
            // Open color picker action icon
            IconButton(onClick = onOpenColorPickerClick) {
                Icon(
                    imageVector = TablerIcons.Palette,
                    contentDescription = "Open Color Picker Button",
                    tint = MaterialTheme.colors.onPrimary
                )
            }
            if(enableTrash){
                IconButton(onClick = onDeleteNoteClick){
                    Icon(
                        imageVector = Octicons.Inbox24,
                        tint = MaterialTheme.colors.onPrimary,
                        contentDescription = "Trash Note Button"
                    )
                }
            }
            if(enablePermaDelete){
                //Restore Function
                IconButton(onClick = onRestoreNote ) {
                    Icon(
                        imageVector = Octicons.People24, //TODO: Change this to "unarchive"
                        tint = MaterialTheme.colors.onPrimary,
                        contentDescription = "Restore Note Button"
                    )

                }

                IconButton(onClick = onPermaDeleteNote){
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

@Composable
private fun SaveNoteContent(
    note: NoteProperty,
    onNoteChange: (NoteProperty) -> Unit,
    onOpenColorPickerClick: () -> Unit
) {
    val noteScrollState = rememberScrollState(100)

    LaunchedEffect(noteScrollState){
        println(noteScrollState.value)
    }

    Column(modifier = Modifier.fillMaxSize().padding(top=4.dp)) {
        ContentTextField(
            modifier = Modifier.heightIn(52.dp,52.dp),
            label = "Title",
            text = note.title,
            onTextChange = { newTitle ->
                onNoteChange.invoke(note.copy(title = newTitle))
            },
            maxLines = 1
        )
        Box(modifier = Modifier.weight(1f).padding(vertical = 16.dp)) {
            ContentTextField(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(noteScrollState),
                label = "Notes",
                text = note.content,
                onTextChange = { newContent ->
                    onNoteChange.invoke(note.copy(content = newContent))
                }
            )
            VerticalScrollbar(
                modifier = Modifier
                    .padding(start = 0.dp, top = 8.dp, bottom = 8.dp, end = 12.dp)
                    .align(Alignment.CenterEnd),
                adapter = rememberScrollbarAdapter(noteScrollState)
            )
        }

        /*
        NoteCheckOption( isChecked = canBeCheckedOff,
            onCheckedChange = { canBeCheckedOffNewValue ->
                val isCheckedOff: Boolean? = if (canBeCheckedOffNewValue)
                    false else null
                onNoteChange.invoke(note.copy(isCheckedOff = isCheckedOff))
            }
        )*/
        //PickedColor(color = note.color, onOpenColorPickerClick) //TODO: fix coloring in new model
        //PickedColor(color = ColorModel.DEFAULT, onOpenColorPickerClick) //TODO: fix coloring in new model
    }
}
@Preview
@Composable
fun SaveNoteContentPreview() {
    SaveNoteContent(
        note = NoteProperty(title = "Title", content = "content"),
        onNoteChange = {},
        onOpenColorPickerClick = {}
    )
}

@Composable
private fun ContentTextField(
    modifier: Modifier = Modifier,
    label: String,
    text: String,
    onTextChange: (String) -> Unit,
    maxLines: Int = Int.MAX_VALUE
) {
    var contentText by remember { mutableStateOf(text)}
    TextField(
        value = contentText,
        onValueChange = { contentText = it ; onTextChange(contentText)},
        label = { Text(label, fontSize = 14.sp) },
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        colors = TextFieldDefaults.textFieldColors( //"default" Data structure used to define colors
            backgroundColor = MaterialTheme.colors.surface
        ),
        textStyle = TextStyle(color = MaterialTheme.colors.onPrimary, fontSize = 13.sp),
        maxLines = maxLines
    )
}
@Preview
@Composable
fun ContentTextFieldPreview() {
    ContentTextField(
        label = "Title",
        text = "",
        onTextChange = {}
    )
}



/*
@Composable
private fun PickedColor(color: ColorModel, onOpenColorPickerClick: () -> Unit) {
    Row(
        Modifier
            .padding(8.dp)
            .padding(top = 16.dp)
    ) {
        Text(
            text = "Picked color",
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
        )
        NoteColor(
            color = Color.fromHex(color.hex),
            size = 40.dp,
            border = 1.dp,
            modifier = Modifier
                .padding(4.dp)
                .clickable { onOpenColorPickerClick() }
        )
    }
}
@Preview(showBackground = true, backgroundColor = 0xFFFFFF)
@Composable
fun PickedColorPreview() {
    PickedColor(ColorModel.DEFAULT, {})
}
 */

@Composable
private fun NoteCheckOption(
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        Modifier
            .padding(8.dp)
            .padding(top = 16.dp)
    ) {
        Text(
            text = "Enable Note Checkbox",
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}
@Preview
@Composable
fun NoteCheckOptionPreview() {
    NoteCheckOption(false) {}
}

@Preview
@Composable
private fun SaveNoteTopAppBarPreview(){
    SaveNoteTopAppBar(
        enableTrash = true,
        enablePermaDelete = false,
        onBackClick = { },
        onSaveNoteClick = { },
        onOpenColorPickerClick = { },
        onDeleteNoteClick = { },
        onPermaDeleteNote = { },
        onRestoreNote = { }
    )

}

/*
@Composable
fun ColorItem(
    color: ColorModel,
    onColorSelect: (ColorModel) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = {
                    onColorSelect(color)
                }
            )
    ) {
        NoteColor(
            modifier = Modifier.padding(10.dp),
            color = Color.fromHex(color.hex),
            size = 80.dp,
            border = 2.dp
        )
        Text(
            text = color.name,
            fontSize = 22.sp,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .align(Alignment.CenterVertically)
        )
    }
}

@Preview
@Composable
fun ColorItemPreview(){
    ColorItem(color = ColorModel.DEFAULT, onColorSelect = {})
}

@Preview
@Composable
fun ColorPickerPreview() {
    ColorPicker(
        colors = listOf(
            ColorModel.DEFAULT,
            ColorModel.DEFAULT,
            ColorModel.DEFAULT,
        )
    ){}
}


@Composable
private fun ColorPicker(
    colors: List<ColorModel>,
    onColorSelect: (ColorModel) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Color picker",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(8.dp)
        )
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(colors.size) { itemIndex ->
                val color = colors[itemIndex]
                ColorItem(
                    color = color,
                    onColorSelect = onColorSelect
                )
            }
        }
    }
}*/