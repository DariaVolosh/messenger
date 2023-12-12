package com.example.messenger.domain.messages

import com.example.messenger.data.model.Message
import com.example.messenger.data.model.User
import com.example.messenger.data.repositories.MessagesRepository
import com.example.messenger.domain.user.GetCurrentUserObjectUseCase
import javax.inject.Inject

class FetchLastMessagesUseCase @Inject constructor(
    private val messagesRepository: MessagesRepository,
    private val getCurrentUserObjectUseCase: GetCurrentUserObjectUseCase
) {
    suspend fun fetchLastMessages(chats: List<User>, oldLastMessages: MutableList<Message>) {
        val currentUserId = getCurrentUserObjectUseCase.getCurrentUserObject()
        messagesRepository.fetchLastMessages(chats, currentUserId.userId, oldLastMessages)
    }
}