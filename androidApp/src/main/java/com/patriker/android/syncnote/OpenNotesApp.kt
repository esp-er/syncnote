package com.patriker.android.syncnote

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import org.koin.dsl.module
import com.patriker.syncnote.initKoin

/**
 * Application class responsible for initializing Koin DI
 */
class OpenNotesApp : Application() {

  override fun onCreate() {

    super.onCreate()
    initKoin(
      appModule = module {
        single<Context> { this@OpenNotesApp }

        single<SharedPreferences> {
          get<Context>().getSharedPreferences(
            "OpenNotesApp",
            MODE_PRIVATE
          )
        }
      }
    )
  }

}
