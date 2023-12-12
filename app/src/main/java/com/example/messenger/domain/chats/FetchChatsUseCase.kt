package com.example.messenger.domain.chats

import com.example.messenger.data.repositories.ChatsRepository
import com.example.messenger.domain.user.GetCurrentUserObjectUseCase
import javax.inject.Inject

class FetchChatsUseCase @Inject constructor(
    private val getCurrentUserObjectUseCase: GetCurrentUserObjectUseCase,
    private val chatsRepository: ChatsRepository
) {
    suspend fun fetchChats() {
        val currentUser = getCurrentUserObjectUseCase.getCurrentUserObject()
        chatsRepository.fetchChats(currentUser.userId)
    }
}