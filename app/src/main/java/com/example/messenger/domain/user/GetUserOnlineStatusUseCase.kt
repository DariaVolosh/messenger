package com.example.messenger.domain.user

import com.example.messenger.data.repositories.UserRepository
import javax.inject.Inject

class GetUserOnlineStatusUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend fun getUserOnlineStatus(id: String) = userRepository.getOnlineStatus(id)
}