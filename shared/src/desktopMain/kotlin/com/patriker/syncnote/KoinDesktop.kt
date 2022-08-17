package com.patriker.syncnote

import com.raywenderlich.jetnotes.OpenNotesDb
import com.russhwolf.settings.ExperimentalSettingsImplementation
import com.russhwolf.settings.JvmPreferencesSettings
import com.russhwolf.settings.Settings
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import org.koin.dsl.module
import java.util.prefs.Preferences
import java.io.File

@ExperimentalSettingsImplementation
actual val platformModule = module {

    single {
        Preferences.userRoot().node(System.getProperty("user.name", "prefs") + "syncnote")
    }

    single<Settings> {
        JvmPreferencesSettings(get())
    }

    single<SqlDriver> {
        //Note: we can specify path to .db file here
        val homeDir = System.getProperty("user.home", "/")
        val dbDir = File("$homeDir/.SyncNote")
        if(!dbDir.exists())
            dbDir.mkdir()
        val dbFilename = "SyncNote_db.db"
        val dbPath = File("$dbDir/$dbFilename")
        val driver = JdbcSqliteDriver("jdbc:sqlite:$dbPath")
        //TODO: enable automatic creation of this file (if does not exist)
        if(!dbPath.exists()) {
            OpenNotesDb.Schema.create(driver)
        }
        driver
    }
}