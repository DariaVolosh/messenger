package com.example.messenger

import android.app.Application
import com.example.messenger.di.AppComponent
import com.example.messenger.di.DaggerAppComponent
import javax.inject.Inject

class MyApp @Inject constructor() : Application() {
    val appComponent: AppComponent.Factory = DaggerAppComponent.factory()
}