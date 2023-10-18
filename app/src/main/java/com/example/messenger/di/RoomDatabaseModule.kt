package com.example.messenger.di

import android.content.Context
import com.example.messenger.room.UserAndChatDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RoomDatabaseModule {
    @Provides
    @Singleton
    fun provideUserAndChatDatabase(context: Context)= UserAndChatDatabase.getInstance(context)

    @Provides
    @Singleton
    fun provideUserDAO(context: Context) = provideUserAndChatDatabase(context).userDao()

    @Provides
    @Singleton
    fun provideChatDAO(context: Context) = provideUserAndChatDatabase(context).chatDao()
}