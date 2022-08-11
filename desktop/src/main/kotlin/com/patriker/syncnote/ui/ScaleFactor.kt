package com.patriker.syncnote.ui

import java.awt.GraphicsEnvironment
import java.lang.reflect.Method

fun getScaleFactor(): Int {
    try {
        // Use reflection to avoid compile errors on non-macOS environments
        val screen = Class.forName("sun.awt.CGraphicsDevice")
            .cast(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice())
        val getScaleFactor: Method = screen.javaClass.getDeclaredMethod("getScaleFactor")
        val obj: Any = getScaleFactor.invoke(screen)
        if (obj is Int) {
            return obj.toInt()
        }
    } catch (e: Exception) {
        println("Unable to determine screen scale factor.  Defaulting to 1.")
    }
    return 1
}
