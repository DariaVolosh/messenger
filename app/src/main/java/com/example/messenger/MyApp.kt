package com.example.messenger

import android.app.Application
import com.example.messenger.di.AppComponent
import com.example.messenger.di.DaggerAppComponent
import javax.inject.Inject

class MyApp @Inject constructor() : Application() {
    lateinit var appComponent: AppComponent
    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.factory().create(applicationContext)
    }
}