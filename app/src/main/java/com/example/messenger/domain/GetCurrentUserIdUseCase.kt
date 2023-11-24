package com.example.messenger.domain

import com.example.messenger.data.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetCurrentUserIdUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend fun getCurrentUserId() = withContext(Dispatchers.IO) {
        userRepository.getCurrentUserId()
    }
}