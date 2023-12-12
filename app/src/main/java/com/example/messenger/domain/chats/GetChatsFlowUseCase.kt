package com.example.messenger.domain.chats

import com.example.messenger.data.repositories.ChatsRepository
import javax.inject.Inject

class GetChatsFlowUseCase @Inject constructor(
    private val chatsRepository: ChatsRepository
) {
    fun getChatsFlow() = chatsRepository.getChatsFlow()
}