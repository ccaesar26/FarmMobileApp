package com.example.farmmobileapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class FarmApp : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}
