package com.example.messenger.domain.user

import com.example.messenger.data.repositories.UserRepository
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