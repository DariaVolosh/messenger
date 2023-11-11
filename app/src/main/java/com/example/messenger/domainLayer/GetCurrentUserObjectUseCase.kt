package com.example.messenger.domainLayer

import com.example.messenger.data.User
import com.example.messenger.dataLayer.FirebaseUser
import com.example.messenger.dataLayer.UserRepository
import kotlinx.coroutines.Deferred
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetCurrentUserObjectUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {
    init {
        getCurrentUserObject()
    }

    var currentUser: Deferred<User>? = null

    fun getCurrentUserObject() {
        currentUser = userRepository.getCurrentUserId()?.let { id ->
            userRepository.getUserById(id)
        }
    }

    fun getFirebaseUser() = (userRepository as FirebaseUser).getFirebaseUser()
}