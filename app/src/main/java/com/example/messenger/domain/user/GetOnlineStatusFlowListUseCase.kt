package com.example.messenger.domain.user

import com.example.messenger.data.model.User
import com.example.messenger.data.repositories.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetOnlineStatusFlowListUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend fun getOnlineStatusFlowList(list: List<User>) =
        withContext(Dispatchers.IO) {
            userRepository.getOnlineUserStatusFlowList(list)
        }
}