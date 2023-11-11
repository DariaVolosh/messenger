package com.example.messenger.domainLayer

import com.example.messenger.data.User
import com.example.messenger.dataLayer.UserRepository
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import javax.inject.Inject

class SearchUsersByLoginUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val getCurrentUserObjectUseCase: GetCurrentUserObjectUseCase
) {
    fun getUsersByLogin(loginQuery: String): Deferred<List<User>> =
        CoroutineScope(Dispatchers.IO).async {
            val usersDeferred = CompletableDeferred<List<User>>()
            val currentUser = getCurrentUserObjectUseCase.currentUser?.await()
            val result = currentUser?.let { userRepository.getUsersByLogin(loginQuery, it).await() }

            if (result == null) usersDeferred.complete(listOf())
            else usersDeferred.complete(result)

            usersDeferred.await()
        }
}