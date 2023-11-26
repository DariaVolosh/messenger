package com.example.messenger.di.modules

import com.example.messenger.data.repositories.ChatsRepository
import com.example.messenger.data.repositories.FirebaseChats
import com.example.messenger.data.repositories.FirebaseImages
import com.example.messenger.data.repositories.FirebaseMessages
import com.example.messenger.data.repositories.FirebaseUser
import com.example.messenger.data.repositories.ImagesRepository
import com.example.messenger.data.repositories.MessagesRepository
import com.example.messenger.data.repositories.SharedPreferencesUserSettingsRepository
import com.example.messenger.data.repositories.UserRepository
import com.example.messenger.data.repositories.UserSettingsRepository
import dagger.Binds
import dagger.Module

@Module
abstract class RepositoriesModule {
    @Binds
    abstract fun bindUserRepository(firebaseUserRepository: FirebaseUser): UserRepository

    @Binds
    abstract fun bindImagesRepository(firebaseImages: FirebaseImages): ImagesRepository

    @Binds
    abstract fun bindChatsRepository(firebaseChats: FirebaseChats): ChatsRepository

    @Binds
    abstract fun bindMessagesRepository(firebaseMessages: FirebaseMessages): MessagesRepository

    @Binds
    abstract fun bindUserSettingsRepository(
        sharedPrefsRepository: SharedPreferencesUserSettingsRepository
    ): UserSettingsRepository
}