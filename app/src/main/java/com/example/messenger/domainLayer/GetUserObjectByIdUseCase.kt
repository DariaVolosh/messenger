package com.example.messenger.domainLayer

import com.example.messenger.dataLayer.UserRepository
import javax.inject.Inject

class GetUserObjectByIdUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    fun getUserById(id: String) = userRepository.getUserById(id)
}