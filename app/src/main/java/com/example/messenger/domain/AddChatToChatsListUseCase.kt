package com.example.messenger.domain

import com.example.messenger.data.ChatsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AddChatToChatsListUseCase @Inject constructor(
    private val chatsRepository: ChatsRepository,
) {
    suspend fun addChatToChatsList(currentUserId: String, friendId: String) {
        withContext(Dispatchers.IO) {
            chatsRepository.addChatToChatsList(currentUserId, friendId)
        }
    }
}