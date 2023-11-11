package com.example.messenger.domainLayer

import com.example.messenger.dataLayer.ChatsRepository
import javax.inject.Inject

class GetConversationReferenceUseCase @Inject constructor(
    private val chatsRepository: ChatsRepository
) {
    fun getConversationReference(currentUserId: String, friendId: String) =
        chatsRepository.getConversationReference(currentUserId, friendId)
}