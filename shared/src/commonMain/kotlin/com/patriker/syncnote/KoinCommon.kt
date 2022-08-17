package com.patriker.syncnote


import com.patriker.syncnote.data.Repository
import com.patriker.syncnote.data.DatabaseHelper
import com.patriker.syncnote.data.ExternDatabaseHelper
import com.patriker.syncnote.data.ExternRepository
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module

expect val platformModule: Module



object Modules {
    val core = module {
        //factory { Platform() }
        factory { DatabaseHelper(get()) }
        factory{ ExternDatabaseHelper(get()) }
    }

    val repositories = module {
        factory { Repository(get()) }
        factory { ExternRepository(get()) }
    }
    val viewModel = module {
        //(repository, externRepository, Settings)
        single { MainViewModel(get(), get(), get()) }
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