package com.example.messenger.domain.user

import com.example.messenger.data.model.User
import com.example.messenger.data.repositories.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetCurrentUserObjectUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    fun getFirebaseUser() = userRepository.getFirebaseUser()
    suspend fun getCurrentUserObject(): User =
        withContext(Dispatchers.IO) {
            userRepository.getCurrentUserId()?.let { id ->
                userRepository.getUserById(id)
            } ?: User()
        }
}