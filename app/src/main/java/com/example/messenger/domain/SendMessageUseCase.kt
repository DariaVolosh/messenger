package com.example.messenger.domain

import com.example.messenger.data.Message
import com.example.messenger.data.MessagesRepository
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(
    private val messagesRepository: MessagesRepository
){
    suspend fun sendMessage(message: Message, conversationReference: DatabaseReference) {
        withContext(Dispatchers.IO) {
            messagesRepository.sendMessage(message, conversationReference)
        }
    }
}