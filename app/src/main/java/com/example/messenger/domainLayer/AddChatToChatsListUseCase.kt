package com.example.messenger.domainLayer

import com.example.messenger.dataLayer.ChatsRepository
import javax.inject.Inject

class AddChatToChatsListUseCase @Inject constructor(
    private val chatsRepository: ChatsRepository,
) {
    fun addChatToChatsList(currentUserId: String, friendId: String) {
        chatsRepository.addChatToChatsList(currentUserId, friendId)
    }
}