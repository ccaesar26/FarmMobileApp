package com.example.farmmobileapp

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import com.example.farmmobileapp.di.appModule

class FarmApp : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@FarmApp)
            modules(appModule)
        }
    }
}
