package com.raywenderlich.jetnotes

import com.raywenderlich.jetnotes.data.ExternRepository
import com.raywenderlich.jetnotes.data.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import com.russhwolf.settings.Settings

expect class MainViewModel(repository: Repository, cacheRepository: ExternRepository, appConfig: Settings, getCorScope: () -> CoroutineScope = { GlobalScope })
//TODO: Find better way to inject the coroutinescope
