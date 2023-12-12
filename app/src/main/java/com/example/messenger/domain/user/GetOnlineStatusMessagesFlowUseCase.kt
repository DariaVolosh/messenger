package com.example.messenger.domain.user

import com.example.messenger.data.repositories.UserRepository
import javax.inject.Inject

class GetOnlineStatusMessagesFlowUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    fun getOnlineStatusMessagesFlow() = userRepository.getOnlineStatusMessagesFlow()
}