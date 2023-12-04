package com.example.messenger.domain.user

import com.example.messenger.data.model.User
import com.example.messenger.data.repositories.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class EmitOnlineValuesUseCase @Inject constructor(
    private val userRepository: UserRepository
){
    fun emitOnlineValues(flows: List<Flow<Boolean>>, users: List<User>) {
        userRepository.emitOnlineValues(flows, users)
    }
}