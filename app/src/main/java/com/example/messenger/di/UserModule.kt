package com.example.messenger.di

import com.example.messenger.data.User
import com.example.messenger.domain.GetCurrentUserObjectUseCase
import dagger.Module
import dagger.Provides

@Module
class UserModule {

    @Provides
    suspend fun provideCurrentUser(getCurrentUserObjectUseCase: GetCurrentUserObjectUseCase): User =
        getCurrentUserObjectUseCase.currentUser.await()
}