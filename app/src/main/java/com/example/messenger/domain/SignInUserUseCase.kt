package com.example.messenger.domain

import com.example.messenger.data.repositories.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SignInUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
   suspend fun signInUser(email: String, password: String): Boolean =
        withContext(Dispatchers.IO) {
            userRepository.signInUser(email, password)
        }
}