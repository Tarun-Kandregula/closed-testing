package com.tk.a12testers14days

import android.app.Application
import com.tk.a12testers14days.di.appModule
import com.tk.a12testers14days.di.networkModule
import com.tk.a12testers14days.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class TwelveTestersApp : Application() {
    override fun onCreate() {
        super.onCreate()
        
        startKoin {
            androidLogger()
            androidContext(this@TwelveTestersApp)
            modules(appModule, networkModule, viewModelModule)
        }
    }
}
