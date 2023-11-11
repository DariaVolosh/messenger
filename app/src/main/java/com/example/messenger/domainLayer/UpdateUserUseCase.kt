package com.example.messenger.domainLayer

import com.example.messenger.data.User
import com.example.messenger.dataLayer.UserRepository
import javax.inject.Inject

class UpdateUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    fun updateUser(updatedUser: User) {
        userRepository.updateUser(updatedUser)
    }
}