package com.example.messenger.domainLayer

import com.example.messenger.dataLayer.UserRepository
import javax.inject.Inject

class SignInUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    fun signInUser(email: String, password: String) {
        userRepository.signInUser(email, password)
    }
}