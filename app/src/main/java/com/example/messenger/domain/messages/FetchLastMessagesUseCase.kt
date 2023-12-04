package com.example.messenger.domain.messages

import com.example.messenger.data.model.Message
import com.example.messenger.data.model.User
import com.example.messenger.data.repositories.MessagesRepository
import com.example.messenger.domain.user.GetCurrentUserObjectUseCase
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
            val currentUserId = getCurrentUserObjectUseCase.getCurrentUserObject()
            lastMessages.complete(
                messagesRepository.fetchLastMessages(chats, currentUserId.userId)
            )

            lastMessages.await()
        }
}