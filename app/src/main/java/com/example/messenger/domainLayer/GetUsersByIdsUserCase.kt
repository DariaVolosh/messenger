package com.example.messenger.domainLayer

import com.example.messenger.dataLayer.UserRepository
import javax.inject.Inject

class GetUsersByIdsUserCase @Inject constructor(
    val userRepository: UserRepository
) {
    fun getUsersByIds(ids: List<String>) = userRepository.getUsersByIds(ids)
}