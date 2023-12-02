package com.example.messenger.domain

import com.example.messenger.data.repositories.UserRepository
import javax.inject.Inject

class SetUserOnlineStatusUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    fun setOnlineStatus(online: Boolean, id: String) {
        userRepository.setOnlineStatus(online, id)
    }
}