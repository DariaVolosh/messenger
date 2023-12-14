package com.example.messenger.di

import android.content.Context
import android.view.LayoutInflater
import com.example.messenger.MainActivity
import com.example.messenger.MyApp
import com.example.messenger.di.modules.FirebaseModule
import com.example.messenger.di.modules.ImageLoaderModule
import com.example.messenger.di.modules.MapperModule
import com.example.messenger.di.modules.RepositoriesModule
import com.example.messenger.di.modules.RoomDatabaseModule
import com.example.messenger.di.modules.SharedPrefsModule
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Component(modules = [
    RoomDatabaseModule::class,
    FirebaseModule::class,
    RepositoriesModule::class,
    ImageLoaderModule::class,
    MapperModule::class,
    SharedPrefsModule::class
])
@Singleton
interface AppComponent {
    fun inject(myApp: MyApp)
    fun inject(mainActivity: MainActivity)

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance context: Context,
            @BindsInstance layoutInflater: LayoutInflater
        ): AppComponent
    }
}