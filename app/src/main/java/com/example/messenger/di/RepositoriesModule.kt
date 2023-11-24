package com.example.messenger.di

import com.example.messenger.data.ChatsRepository
import com.example.messenger.data.FirebaseChats
import com.example.messenger.data.FirebaseImages
import com.example.messenger.data.FirebaseMessages
import com.example.messenger.data.FirebaseUser
import com.example.messenger.data.ImagesRepository
import com.example.messenger.data.MessagesRepository
import com.example.messenger.data.UserRepository
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