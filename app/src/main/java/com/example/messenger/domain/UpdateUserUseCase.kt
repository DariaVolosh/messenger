package com.example.messenger.domain

import com.example.messenger.data.User
import com.example.messenger.data.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UpdateUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend fun updateUser(updatedUser: User) = withContext(Dispatchers.IO) {
        userRepository.updateUser(updatedUser)
    }
}