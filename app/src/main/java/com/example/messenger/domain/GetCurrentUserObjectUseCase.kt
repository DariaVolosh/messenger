package com.example.messenger.domain

import com.example.messenger.data.model.User
import com.example.messenger.data.repositories.UserRepository
import javax.inject.Inject

class GetCurrentUserObjectUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    fun getFirebaseUser() = userRepository.getFirebaseUser()
    suspend fun getCurrentUserObject(): User =
        userRepository.getCurrentUserId()?.let { id ->
            userRepository.getUserById(id)
        } ?: User()
}