package com.example.messenger.di.modules

import com.example.messenger.data.model.User
import com.example.messenger.domain.GetCurrentUserObjectUseCase
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.runBlocking

@Module
class UserModule {
    @Provides
    fun provideCurrentUser(getCurrentUserObjectUseCase: GetCurrentUserObjectUseCase): User =
        runBlocking {
            getCurrentUserObjectUseCase.currentUser.await()
        }
}