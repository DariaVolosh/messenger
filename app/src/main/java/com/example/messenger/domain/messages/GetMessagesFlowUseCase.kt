package com.example.messenger.domain.messages

import com.example.messenger.data.repositories.MessagesRepository
import javax.inject.Inject

class GetMessagesFlowUseCase @Inject constructor(
    private val messagesRepository: MessagesRepository
) {
    fun getMessagesFlowUseCase() = messagesRepository.getMessagesFlow()
}