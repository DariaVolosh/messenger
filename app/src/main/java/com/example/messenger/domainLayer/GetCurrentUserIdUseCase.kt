package com.example.messenger.domainLayer

import com.example.messenger.dataLayer.UserRepository
import javax.inject.Inject

class GetCurrentUserIdUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    fun getCurrentUserId() = userRepository.getCurrentUserId()
}