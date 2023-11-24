package com.example.messenger.domain

import com.example.messenger.data.User
import com.example.messenger.data.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SearchUsersByLoginUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val getCurrentUserObjectUseCase: GetCurrentUserObjectUseCase
) {
    suspend fun getUsersByLogin(loginQuery: String): List<User> =
        withContext(Dispatchers.IO) {
            val currentUser = getCurrentUserObjectUseCase.currentUser.await()
            userRepository.getUsersByLogin(loginQuery, currentUser)
        }
}