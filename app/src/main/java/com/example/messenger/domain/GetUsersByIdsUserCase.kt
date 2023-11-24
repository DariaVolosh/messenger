package com.example.messenger.domain

import com.example.messenger.data.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetUsersByIdsUserCase @Inject constructor(
    val userRepository: UserRepository
) {
    suspend fun getUsersByIds(ids: List<String>) =
        withContext(Dispatchers.IO) {
            userRepository.getUsersByIds(ids)
        }
}