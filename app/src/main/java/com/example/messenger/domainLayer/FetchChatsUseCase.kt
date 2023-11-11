package com.example.messenger.domainLayer

import com.example.messenger.data.User
import com.example.messenger.dataLayer.ChatsRepository
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import javax.inject.Inject

class FetchChatsUseCase @Inject constructor(
    private val getCurrentUserObjectUseCase: GetCurrentUserObjectUseCase,
    private val chatsRepository: ChatsRepository
) {
    fun fetchChats(): Deferred<List<User>> =
        CoroutineScope(Dispatchers.IO).async {
            val usersDeferred = CompletableDeferred<List<User>>()

            val currentUser = getCurrentUserObjectUseCase.currentUser?.await()
            val result = currentUser?.let { chatsRepository.fetchChats(it.userId).await() }
            if (result == null) usersDeferred.complete(listOf())
            else usersDeferred.complete(result)

            usersDeferred.await()
        }
}