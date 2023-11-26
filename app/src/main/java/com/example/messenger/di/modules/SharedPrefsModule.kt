package com.example.messenger.di.modules

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides

@Module
class SharedPrefsModule {
    @Provides
    fun provideSharedPreferences(context: Context): SharedPreferences =
        context.getSharedPreferences("colors", Context.MODE_PRIVATE)
}