package com.example.messenger.domain.user

import com.example.messenger.data.model.User
import com.example.messenger.data.repositories.UserRepository
import javax.inject.Inject

class EmitChatsOnlineValuesUseCase @Inject constructor(
    private val userRepository: UserRepository
){
    fun emitOnlineValues(users: List<User>, oldStatusList: MutableList<Boolean>) {
        userRepository.emitChatsOnlineValues(users, oldStatusList)
    }
}