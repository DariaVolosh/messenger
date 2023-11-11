package com.example.messenger.domainLayer

import com.example.messenger.data.Message
import com.example.messenger.data.User
import com.example.messenger.dataLayer.MessagesRepository
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class FetchLastMessagesUseCase @Inject constructor(
    private val messagesRepository: MessagesRepository,
    private val getCurrentUserObjectUseCase: GetCurrentUserObjectUseCase
) {
    fun fetchLastMessages(chats: List<User>): Deferred<List<Message>> {
        val lastMessages = CompletableDeferred<List<Message>>()
        CoroutineScope(Dispatchers.IO).launch {
            val currentUserId = getCurrentUserObjectUseCase.currentUser?.await()
            if (currentUserId != null) {
                lastMessages.complete(
                    messagesRepository.fetchLastMessages(chats, currentUserId.userId).await()
                )
            } else {
                lastMessages.complete(listOf())
            }
        }

        return lastMessages
    }
}