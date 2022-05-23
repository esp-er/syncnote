package com.raywenderlich.jetnotes

//import com.russhwolf.settings.AndroidSettings
//import com.russhwolf.settings.Settings
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import com.raywenderlich.jetnotes.MainViewModel
import com.raywenderlich.jetnotes.OpenNotesDb
import org.koin.core.context.GlobalContext.get
import org.koin.dsl.module

//"actual" in mpp
actual val platformModule = module {

 /*   single<Settings> {
        AndroidSettings(get())
    }*/

    single<SqlDriver> {
        AndroidSqliteDriver(OpenNotesDb.Schema, get(), "OpenNotesDb")
    }
}
