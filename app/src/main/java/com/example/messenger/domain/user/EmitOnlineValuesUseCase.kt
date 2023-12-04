package com.example.messenger.domain.user

import com.example.messenger.data.model.User
import com.example.messenger.data.repositories.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class EmitOnlineValuesUseCase @Inject constructor(
    private val userRepository: UserRepository
){
    suspend fun emitOnlineValues(flows: List<Flow<Boolean>>, users: List<User>) =
        withContext(Dispatchers.IO) {
            userRepository.emitOnlineValues(flows, users)
        }
}