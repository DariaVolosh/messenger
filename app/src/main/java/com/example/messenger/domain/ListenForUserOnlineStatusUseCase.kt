package com.example.messenger.domain

import com.example.messenger.data.repositories.UserRepository
import javax.inject.Inject

class ListenForUserOnlineStatusUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend fun getOnlineStatus(id: String) = userRepository.onUserOnlineStatusListener(id)
}