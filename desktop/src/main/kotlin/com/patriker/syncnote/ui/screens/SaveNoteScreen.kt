package com.patriker.syncnote.ui.screens
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.*
import androidx.compose.material.TextFieldDefaults.indicatorLine
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.sp
//import com.patriker.syncnote.ui.components.NoteColor
import com.patriker.syncnote.MainViewModel
import com.patriker.syncnote.domain.NoteProperty
import com.patriker.syncnote.routing.NotesRouter
import com.patriker.syncnote.routing.Screen
import compose.icons.Octicons
import compose.icons.octicons.ArrowLeft24
import compose.icons.octicons.Inbox24
import compose.icons.octicons.Check24
import compose.icons.octicons.People24
import compose.icons.octicons.Trash24


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
            IconButton(onClick = onSaveNoteClick) {
                Icon(
                    imageVector = Octicons.Check24,
                    tint = MaterialTheme.colors.onPrimary,
                    contentDescription = "Save Note"
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
                        imageVector = Octicons.People24,
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
    val noteScrollState = rememberScrollState(0)


    Column(modifier = Modifier.fillMaxSize().padding(top=4.dp)) {
        ContentTextField(
            modifier = Modifier.heightIn(42.dp,42.dp),
            label = "Title",
            text = note.title,
            onTextChange = { newTitle ->
                onNoteChange.invoke(note.copy(title = newTitle))
            },
            maxLines = 1
        )
        Box(modifier = Modifier.weight(1f).padding(vertical = 8.dp)) {
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
    val focusManager = LocalFocusManager.current
    var isFocused by remember{ mutableStateOf(false) }
    var contentText by remember { mutableStateOf(text)}
    NoteTextField(
        value = contentText,
        onValueChange = { contentText = it ; onTextChange(contentText)},
        label = { Row{
            if(!isFocused) Text(label, fontSize = 12.sp) else Text(label, fontSize = 12.sp)
        }},
        placeholder = {},
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 0.dp)
            .onFocusChanged { isFocused = it.isFocused },
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

@Composable
fun NoteTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions(),
    singleLine: Boolean = false,
    maxLines: Int = Int.MAX_VALUE,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape =
        MaterialTheme.shapes.small.copy(bottomEnd = ZeroCornerSize, bottomStart = ZeroCornerSize),
    colors: TextFieldColors = TextFieldDefaults.textFieldColors()
) {
    // If color is not provided via the text style, use content color as a default
    val textColor = textStyle.color.takeOrElse {
        colors.textColor(enabled).value
    }
    val mergedTextStyle = textStyle.merge(TextStyle(color = textColor))

    @OptIn(ExperimentalMaterialApi::class)
    (BasicTextField(
        value = value,
        modifier = modifier
            .background(colors.backgroundColor(enabled).value, shape)
            .indicatorLine(enabled, isError, interactionSource, colors)
            .defaultMinSize(
                minWidth = TextFieldDefaults.MinWidth,
                minHeight = 40.dp
            ),
        onValueChange = onValueChange,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = mergedTextStyle,
        cursorBrush = SolidColor(colors.cursorColor(isError).value),
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        interactionSource = interactionSource,
        singleLine = singleLine,
        maxLines = maxLines,
        decorationBox = @Composable { innerTextField ->
            // places leading icon, text field with label and placeholder, trailing icon
            TextFieldDefaults.TextFieldDecorationBox(
                value = value,
                visualTransformation = visualTransformation,
                innerTextField = innerTextField,
                placeholder = placeholder,
                label = label,
                leadingIcon = leadingIcon,
                trailingIcon = trailingIcon,
                singleLine = singleLine,
                enabled = enabled,
                isError = isError,
                interactionSource = interactionSource,
                colors = colors,
                contentPadding = PaddingValues(start = 8.dp, top=14.dp, bottom = 8.dp, end=8.dp)
            )
        }
    ))
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