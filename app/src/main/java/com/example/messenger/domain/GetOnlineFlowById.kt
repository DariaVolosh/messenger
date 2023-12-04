package com.example.messenger.domain

import com.example.messenger.data.repositories.UserRepository
import javax.inject.Inject

class GetOnlineFlowById @Inject constructor(
    private val userRepository: UserRepository
) {
    fun getOnlineFlowById(id: String) = userRepository.getOnlineFlowById(id)
}