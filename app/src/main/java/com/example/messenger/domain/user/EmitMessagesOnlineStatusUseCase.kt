package com.example.messenger.domain.user

import com.example.messenger.data.repositories.UserRepository
import javax.inject.Inject

class EmitMessagesOnlineStatusUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    fun emitMessagesOnlineStatusUseCase(userId: String) {
        userRepository.emitMessagesOnlineStatus(userId)
    }
}