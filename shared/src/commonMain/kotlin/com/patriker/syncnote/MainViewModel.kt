package com.patriker.syncnote

import com.patriker.syncnote.data.ExternRepository
import com.patriker.syncnote.data.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import com.russhwolf.settings.Settings

expect class MainViewModel(repository: Repository, cacheRepository: ExternRepository, appConfig: Settings, getCorScope: () -> CoroutineScope = { GlobalScope })
//TODO: Find better way to inject the coroutinescope
