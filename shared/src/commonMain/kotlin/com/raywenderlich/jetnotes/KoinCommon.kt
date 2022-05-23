package com.raywenderlich.jetnotes


import com.raywenderlich.jetnotes.data.Repository
import com.raywenderlich.jetnotes.data.DatabaseHelper
//import com.raywenderlich.organize.presentation.AboutViewModel
//import com.raywenderlich.organize.presentation.RemindersViewModel
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module

expect val platformModule: Module



object Modules {
    val core = module {
//        factory { Platform() }
        factory { DatabaseHelper(get()) }
    }

    val repositories = module {
        factory { Repository(get()) }
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