package com.example.messenger.domain

import com.example.messenger.data.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetUserObjectByIdUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend fun getUserById(id: String) =
        withContext(Dispatchers.IO) {
            userRepository.getUserById(id)
        }
}