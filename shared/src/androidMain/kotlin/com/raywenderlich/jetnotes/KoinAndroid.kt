package com.raywenderlich.jetnotes

import com.russhwolf.settings.AndroidSettings
import com.russhwolf.settings.Settings
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import org.koin.dsl.module

actual val platformModule = module {

    single<Settings> {
        AndroidSettings(get())
    }

    single<SqlDriver> {
        AndroidSqliteDriver(OpenNotesDb.Schema, get(), "OpenNotesDb")
    }


    /*
    single<SyncClient>{
        SyncClient(get())
    }*/


}
