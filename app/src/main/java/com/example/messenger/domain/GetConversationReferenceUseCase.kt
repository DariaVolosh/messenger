package com.example.messenger.domain

import com.example.messenger.data.repositories.ChatsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetConversationReferenceUseCase @Inject constructor(
    private val chatsRepository: ChatsRepository
) {
    suspend fun getConversationReference(currentUserId: String, friendId: String) =
        withContext(Dispatchers.IO) {
            chatsRepository.getConversationReference(currentUserId, friendId)
        }
}