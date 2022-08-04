package com.raywenderlich.jetnotes

import com.russhwolf.settings.ExperimentalSettingsImplementation
import com.russhwolf.settings.JvmPreferencesSettings
import com.russhwolf.settings.Settings
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import org.koin.dsl.module
import java.util.prefs.Preferences

@ExperimentalSettingsImplementation
actual val platformModule = module {

    single {
        Preferences.userRoot()
    }

    single<Settings> {
        JvmPreferencesSettings(get())
    }

    single<SqlDriver> {
        //Note: we can specify path to .db file here
        val driver = JdbcSqliteDriver("jdbc:sqlite:OpenNotesDb.db")
        //TODO: enable automatic creation of this file (if does not exist)
        //OpenNotesDb.Schema.create(driver)
        driver
    }
}