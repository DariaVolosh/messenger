package com.example.messenger.domain.user

import com.example.messenger.data.repositories.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SetUserOnlineStatusUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend fun setOnlineStatus(online: Boolean, id: String) {
        withContext(Dispatchers.IO) {
            userRepository.setOnlineStatus(online, id)
        }
    }
}