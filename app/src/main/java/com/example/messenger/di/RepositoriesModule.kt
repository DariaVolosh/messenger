package com.example.messenger.di

import com.example.messenger.data.repositories.ChatsRepository
import com.example.messenger.data.repositories.FirebaseChats
import com.example.messenger.data.repositories.FirebaseImages
import com.example.messenger.data.repositories.FirebaseMessages
import com.example.messenger.data.repositories.FirebaseUser
import com.example.messenger.data.repositories.ImagesRepository
import com.example.messenger.data.repositories.MessagesRepository
import com.example.messenger.data.repositories.UserRepository
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
}