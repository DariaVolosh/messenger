package com.example.messenger.di

import com.example.messenger.dataLayer.ChatsRepository
import com.example.messenger.dataLayer.FirebaseChats
import com.example.messenger.dataLayer.FirebaseImages
import com.example.messenger.dataLayer.FirebaseMessages
import com.example.messenger.dataLayer.FirebaseUser
import com.example.messenger.dataLayer.ImagesRepository
import com.example.messenger.dataLayer.MessagesRepository
import com.example.messenger.dataLayer.UserRepository
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