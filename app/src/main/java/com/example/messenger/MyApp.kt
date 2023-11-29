package com.example.messenger

import android.app.Application
import android.content.Context
import android.view.LayoutInflater
import com.example.messenger.di.AppComponent
import com.example.messenger.di.DaggerAppComponent
import javax.inject.Inject


class MyApp @Inject constructor() : Application() {
    lateinit var appComponent: AppComponent
    fun createAppComponent(context: Context, layoutInflater: LayoutInflater) {
        appComponent = DaggerAppComponent.factory().create(context, layoutInflater)
    }
}