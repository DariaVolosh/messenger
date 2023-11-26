package com.example.messenger.di.modules

import android.content.Context
import androidx.room.Room
import com.example.messenger.room.UserAndChatDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RoomDatabaseModule {
    @Provides
    @Singleton
    fun provideUserAndChatDatabase(context: Context): UserAndChatDatabase =
        Room.databaseBuilder(
            context,
            UserAndChatDatabase::class.java,
            "userAndChatDatabase.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun provideUserDAO(context: Context) = provideUserAndChatDatabase(context).userDao()

    @Provides
    @Singleton
    fun provideChatDAO(context: Context) = provideUserAndChatDatabase(context).chatDao()
}