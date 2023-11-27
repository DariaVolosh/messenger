package com.example.messenger.domain

import com.example.messenger.data.Message
import com.example.messenger.data.User
import com.example.messenger.data.repositories.MessagesRepository
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FetchLastMessagesUseCase @Inject constructor(
    private val messagesRepository: MessagesRepository,
    private val getCurrentUserObjectUseCase: GetCurrentUserObjectUseCase
) {
    suspend fun fetchLastMessages(chats: List<User>): List<Message> =
        withContext(Dispatchers.IO) {
            val lastMessages = CompletableDeferred<List<Message>>()
            val currentUserId = getCurrentUserObjectUseCase.currentUser.await()
            if (currentUserId != null) {
                lastMessages.complete(
                    messagesRepository.fetchLastMessages(chats, currentUserId.userId)
                )
            } else {
                lastMessages.complete(listOf())
            }

            lastMessages.await()
        }
}