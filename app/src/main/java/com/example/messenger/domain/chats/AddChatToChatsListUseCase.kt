package com.example.messenger.domain.chats

import com.example.messenger.data.repositories.ChatsRepository
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