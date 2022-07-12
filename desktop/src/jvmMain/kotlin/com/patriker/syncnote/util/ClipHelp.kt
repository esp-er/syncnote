package com.patriker.syncnote.util

import java.awt.datatransfer.StringSelection
import java.awt.Toolkit
import java.awt.datatransfer.Clipboard


fun setClipboard(text: String) { //TODO: add this as expected and actual funcs in shared code
    val stringSelection = StringSelection(text)
    val clipboard : Clipboard = Toolkit.getDefaultToolkit().systemClipboard
    clipboard.setContents(stringSelection, null);
}