package com.example.messenger.domain

import com.example.messenger.data.model.User
import com.example.messenger.data.repositories.ChatsRepository
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FetchChatsUseCase @Inject constructor(
    private val getCurrentUserObjectUseCase: GetCurrentUserObjectUseCase,
    private val chatsRepository: ChatsRepository
) {
    suspend fun fetchChats(): List<User> =
        withContext(Dispatchers.IO) {
            val usersDeferred = CompletableDeferred<List<User>>()

            val currentUser = getCurrentUserObjectUseCase.currentUser.await()
            val result = currentUser.let { chatsRepository.fetchChats(it.userId) }
            usersDeferred.complete(result)

            usersDeferred.await()
        }
}