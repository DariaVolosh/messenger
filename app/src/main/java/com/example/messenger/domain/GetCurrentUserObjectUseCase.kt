package com.example.messenger.domain

import com.example.messenger.data.FirebaseUser
import com.example.messenger.data.User
import com.example.messenger.data.UserRepository
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetCurrentUserObjectUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {

    var currentUser: CompletableDeferred<User> = CompletableDeferred()

    init {
        getCurrentUserObject()
    }

    fun getCurrentUserObject() {
        CoroutineScope(Dispatchers.IO).launch {
            userRepository.getCurrentUserId()?.let { id ->
                val user = userRepository.getUserById(id)
                currentUser.complete(user)
            }
        }
    }

    fun getFirebaseUser() = (userRepository as FirebaseUser).getFirebaseUser()
}