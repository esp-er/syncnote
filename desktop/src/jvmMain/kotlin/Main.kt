import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import androidx.compose.ui.window.MenuBar
import androidx.compose.runtime.*
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyShortcut
import com.raywenderlich.jetnotes.initKoin
import com.raywenderlich.jetnotes.MainViewModel
import com.raywenderlich.jetnotes.domain.NEW_UUID
import com.raywenderlich.jetnotes.domain.NoteProperty
import kotlinx.datetime.Instant
import org.koin.core.Koin
import java.lang.Thread.sleep

//import com.raywenderlich.compose.theme.AppTheme
//import com.raywenderlich.compose.ui.MainView

data class WindowInfo(val windowName: String, val windowState: WindowState)

lateinit var koin: Koin

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    koin = initKoin().koin

    val m = MainViewModel(koin.get())

    repeat(100) {
        m.saveNote(
            NoteProperty(
                id = NEW_UUID,
                title = "hej",
                content = "mung",
                colorId = 0,
                false,
                false,
                false
            )
        )
    }

    m.notes.forEach{ println(it.title)}
    application {

        //Set up multiple windows
        var initialized by remember { mutableStateOf(false) }
        var windowCount by remember { mutableStateOf(1) }
        val windowList = remember { SnapshotStateList<WindowInfo>() }
        if (!initialized) {
            windowList.add(WindowInfo("Timezone-${windowCount}",
                rememberWindowState()))
            initialized = true
        }

        windowList.forEachIndexed { i, window ->
            Window(

                onCloseRequest = {
                    windowList.removeAt(i)
                },

                state = windowList[i].windowState,
                title = windowList[i].windowName
            ) {

                Surface {

                    MenuBar {
                        Menu("File", mnemonic = 'F') {
                            val nextWindowState = rememberWindowState()
                            Item(
                                "New", onClick = {
                                    windowCount++
                                    windowList.add(
                                        WindowInfo(
                                            "Timezone-${windowCount}",
                                            nextWindowState
                                        )
                                    )
                                },
                                shortcut = KeyShortcut(Key.N, ctrl = true)
                            )
                            Item(
                                "Open", onClick = { },
                                shortcut = KeyShortcut(Key.O, ctrl = true)
                            )
                            Item(
                                "Close",
                                onClick = { windowList.removeAt(i) },
                                shortcut = KeyShortcut(Key.W, ctrl = true)
                            )
                            Item(
                                "Save", onClick = { },
                                shortcut = KeyShortcut(Key.S, ctrl = true)
                            )
                            Separator()
                            Item("Exit", onClick = { windowList.clear() },)
                        }
                        Menu("Edit", mnemonic = 'E') {
                            Item(
                                "Cut", onClick = { },
                                shortcut = KeyShortcut(Key.X, ctrl = true)
                            )
                            Item(
                                "Copy", onClick = { },
                                shortcut = KeyShortcut(Key.C, ctrl = true)
                            )
                            Item(
                                "Paste", onClick = { },
                                shortcut = KeyShortcut(Key.V, ctrl = true)
                            )
                        }
                    }
                }

                Surface(modifier = Modifier.fillMaxSize()) {
                    MaterialTheme () {
                        Text("hej")
                    }
                }
            }
        }
    }

}