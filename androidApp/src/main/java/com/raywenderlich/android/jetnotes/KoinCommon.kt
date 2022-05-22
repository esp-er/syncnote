package com.raywenderlich.android.jetnotes


import com.raywenderlich.android.jetnotes.data.repository.NotesRepository
import com.raywenderlich.android.jetnotes.data.database.DatabaseHelper
//import com.raywenderlich.organize.presentation.AboutViewModel
//import com.raywenderlich.organize.presentation.RemindersViewModel
import com.raywenderlich.android.jetnotes.viewmodel.MainViewModel
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module

//expect val platformModule: Module


object Modules {
    val core = module {
//        factory { Platform() }
        factory { DatabaseHelper(get()) }
    }

    val repositories = module {
        factory { NotesRepository(get()) }
    }

    val viewModel= module {
        factory { MainViewModel(get()) }
    }
}

fun initKoin(
    appModule: Module = module { },
    coreModule: Module = Modules.core,
    repositoriesModule: Module = Modules.repositories,
    viewModelsModule: Module = Modules.viewModel,
): KoinApplication = startKoin {
    modules(
        appModule,
        coreModule,
        repositoriesModule,
        viewModelsModule,
        platformModule
    )
}