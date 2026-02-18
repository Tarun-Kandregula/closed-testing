package com.tk.a12testers14days.di

import com.tk.a12testers14days.data.repository.AppRepository
import com.tk.a12testers14days.data.repository.AuthRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {
    single { AuthRepository(get(), androidContext()) }
    single { AppRepository(get()) }
}
